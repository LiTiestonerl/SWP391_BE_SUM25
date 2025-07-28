package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.auth.*;
import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.EmailVerificationToken;
import com.example.smoking_cessation_platform.repository.EmailVerificationTokenRepository;
import com.example.smoking_cessation_platform.repository.RoleRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMemberService userMemberService;

    private final Random random = new Random();

    @Value("${google.client.id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    // Initial verifier when AuthService created
    public AuthService(@Value("${google.client.id}") String googleClientId) {
        this.googleClientId = googleClientId;
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("Tên người dùng đã được sử dụng");
        }


        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName("USER");
                    newRole.setDescription("Người dùng thông thường");
                    return roleRepository.save(newRole);
                });

        String userPublicId = UUID.randomUUID().toString();

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .userPublicId(userPublicId)
                .userName(request.getUserName())
                .password(encodedPassword)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .registrationDate(LocalDateTime.now())
                .role(defaultRole)
                .status("active")
                .isEmailVerified(false)
                .authProvider("LOCAL")
                .providerId(null)
                .build();

        User savedUser = userRepository.save(newUser);
        userMemberService.assignFreePackageToUser(savedUser);

        sendEmailVerificationOtp(savedUser);

        return savedUser;
    }

    /**
     * Gửi mã OTP xác thực email cho người dùng.
     *
     * @param user Người dùng cần gửi mã OTP.
     */
    @Transactional
    public void sendEmailVerificationOtp(User user) {
        String otpCode = String.format("%06d", random.nextInt(1000000));

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .otpCode(otpCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();
        emailVerificationTokenRepository.save(token);

        emailService.sendOtpEmail(user.getEmail(), otpCode);
    }

    /**
     * Gửi lại mã OTP xác thực email cho người dùng đã tồn tại.
     *
     * @param request EmailVerificationRequest chứa email của người dùng.
     */
    @Transactional
    public void resendEmailVerificationOtp(EmailVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));

        if (user.getIsEmailVerified()) {
            throw new RuntimeException("Email này đã được xác thực.");
        }

        Optional<EmailVerificationToken> latestToken = emailVerificationTokenRepository
                .findFirstByUserAndConfirmedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(user, LocalDateTime.now());
        if (latestToken.isPresent()) {
            throw new RuntimeException("Mã OTP đã được gửi gần đây. Vui lòng kiểm tra email hoặc chờ.");
        }

        sendEmailVerificationOtp(user);
    }


    /**
     * Xác minh mã OTP được gửi qua email.
     * Nếu mã hợp lệ và chưa hết hạn, email sẽ được đánh dấu là đã xác thực.
     *
     * @param request VerifyEmailOtpRequest chứa email và mã OTP.
     * @return true nếu xác minh thành công, ngược lại false.
     */
    @Transactional
    public boolean verifyEmailOtp(VerifyEmailOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));

        if (user.getIsEmailVerified()) {
            throw new RuntimeException("Email này đã được xác thực.");
        }

        EmailVerificationToken token = emailVerificationTokenRepository
                .findByUserAndOtpCodeAndExpiresAtAfterAndConfirmedAtIsNull(
                        user, request.getOtp(), LocalDateTime.now())
                .orElse(null);

        if (token != null) {
            token.setConfirmedAt(LocalDateTime.now());
            emailVerificationTokenRepository.save(token);

            user.setIsEmailVerified(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Phương thức đăng ký bằng Google OAuth
     *
     *
     */
    @Transactional
    public AuthResponse registerOrLoginWithGoogle(GoogleAuthRequest request) {
        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Xác minh Google ID Token thất bại: " + e.getMessage());
        }

        if (idToken == null) {
            throw new RuntimeException("Google ID Token không hợp lệ.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String fullName = (String) payload.get("name");
        String userName = email;

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    if ("GOOGLE".equals(existingUser.getAuthProvider()) && googleId.equals(existingUser.getProviderId())) {
                        return existingUser;
                    } else if ("LOCAL".equals(existingUser.getAuthProvider())) {
                        throw new RuntimeException("Email đã được đăng ký. Vui lòng đăng nhập hoặc sử dụng email khác.");
                    } else {
                        throw new RuntimeException("Email đã được đăng ký, vui lòng đăng ký email khác.");
                    }
                })
                .orElseGet(() -> {
                    Role defaultRole = roleRepository.findByRoleName("USER")
                            .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setRoleName("USER");
                                newRole.setDescription("Người dùng thông thường");
                                return roleRepository.save(newRole);
                            });

                    String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                    User newUser = User.builder()
                            .userPublicId(UUID.randomUUID().toString())
                            .userName(userName)
                            .password(dummyPassword)
                            .fullName(fullName)
                            .email(email)
                            .phone(null)
                            .registrationDate(LocalDateTime.now())
                            .role(defaultRole)
                            .status("active")
                            .isEmailVerified(true)
                            .authProvider("GOOGLE")
                            .providerId(googleId)
                            .build();

                    User savedUser = userRepository.save(newUser);
                    userMemberService.assignFreePackageToUser(savedUser);

                    return savedUser;
                });

        // 👉 Sau khi có user, sinh token
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user); // 🔥 Thêm refresh token

        return new AuthResponse(
                user.getUserId(),
                user.getUserPublicId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getStatus(),
                accessToken,
                "Bearer",
                refreshToken // ✅ thêm field này trong AuthResponse
        );
    }

    /**
     * Đăng nhập bằng email hoặc username.
     *  - Kiểm tra tồn tại user
     *  - So khớp mật khẩu (BCrypt)
     *  - Kiểm tra trạng thái / xác minh email (nếu cần)
     *  - Sinh JWT và đóng gói AuthResponse
     */
    @Transactional(readOnly = true)
    public AuthResponse login(@Valid LoginRequest loginRequest) {
        User user = userRepository.findByUserName(loginRequest.getLogin())
                .or(() -> userRepository.findByEmail(loginRequest.getLogin()))
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng với định danh: " + loginRequest.getLogin()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác.");
        }

        if (!"active".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Tài khoản đã bị khoá hoặc không hoạt động.");
        }
        if (Boolean.FALSE.equals(user.getIsEmailVerified())) {
            throw new RuntimeException("Email chưa được xác thực.");
        }

        // ✅ Sinh Access Token
        String accessToken = tokenService.generateAccessToken(user);

        // ✅ Sinh Refresh Token
        String refreshToken = tokenService.generateRefreshToken(user);

        // ✅ Trả về kèm refreshToken
        return new AuthResponse(
                user.getUserId(),
                user.getUserPublicId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getStatus(),
                accessToken,
                "Bearer",
                refreshToken
        );
    }

    public void sendForgotPasswordEmail(EmailVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));
        // Gửi mã OTP xác thực
        sendEmailVerificationOtp(user);
    }

    @Transactional
    public boolean resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));
        // Tìm mã OTP
        EmailVerificationToken token = emailVerificationTokenRepository
                .findByUserAndOtpCodeAndExpiresAtAfterAndConfirmedAtIsNull(
                user, request.getOtp(), LocalDateTime.now())
                .orElse(null);
        if (token != null) {
            // Cập nhật mật khẩu mới
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            // Đánh dấu mã OTP là đã xác thực
            token.setConfirmedAt(LocalDateTime.now());
            emailVerificationTokenRepository.save(token);
            return true;
        } else {
            return false;
        }
    }
}
