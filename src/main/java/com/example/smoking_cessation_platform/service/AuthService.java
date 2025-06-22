package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.EmailVerificationToken;
import com.example.smoking_cessation_platform.dto.auth.RegisterRequest;
import com.example.smoking_cessation_platform.dto.auth.EmailVerificationRequest;
import com.example.smoking_cessation_platform.dto.auth.VerifyEmailOtpRequest;
import com.example.smoking_cessation_platform.dto.auth.GoogleAuthRequest; // Import GoogleAuthRequest
import com.example.smoking_cessation_platform.repository.EmailVerificationTokenRepository;
import com.example.smoking_cessation_platform.repository.RoleRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken; // Import GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier; // Import GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport; // Import NetHttpTransport
import com.google.api.client.json.gson.GsonFactory; // Import GsonFactory
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

    private final Random random = new Random();

    @Value("${google.client.id}")
    private String googleClientId;

    // Khởi tạo GoogleIdTokenVerifier
    private GoogleIdTokenVerifier verifier;

    // Khởi tạo verifier khi AuthService được tạo
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
    public User registerOrLoginWithGoogle(GoogleAuthRequest request) {
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


        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Nếu người dùng đã tồn tại:
            // a. Nếu họ đã đăng ký bằng Google trước đó: Coi như ĐĂNG NHẬP
            if ("GOOGLE".equals(user.getAuthProvider()) && googleId.equals(user.getProviderId())) {
                // Đây là một lần đăng nhập lại bằng Google
                // Logic đăng nhập sẽ ở đây, trả về JWT của ứng dụng
                // ... (tạm thời trả về user để hoàn thành luồng đăng ký)
                return user;
            }
            // b. Nếu họ đăng ký cục bộ hoặc từ nhà cung cấp khác:
            //    Bạn có thể cho phép liên kết tài khoản hoặc ném lỗi trùng email
            else if ("LOCAL".equals(user.getAuthProvider())) {
                // Tùy chọn: Liên kết tài khoản Google vào user hiện có
//                 user.setAuthProvider("GOOGLE");
//                 user.setProviderId(googleId);
//                 userRepository.save(user);
//                 return user; // Trả về user đã được liên kết

                // Hoặc đơn giản hơn: Yêu cầu đăng nhập bằng phương thức cũ
                throw new RuntimeException("Email đã được đăng ký. Vui lòng đăng nhập hoặc sử dụng email khác.");
            }
            else {
                // Email đã tồn tại nhưng từ nhà cung cấp khác
                throw new RuntimeException("Email đã được đăng ký, vui lòng đăng ký email khác.");
            }

        } else {
            // 2. Nếu người dùng chưa tồn tại: Tạo tài khoản mới
            Role defaultRole = roleRepository.findByRoleName("USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("USER");
                        newRole.setDescription("Người dùng thông thường");
                        return roleRepository.save(newRole);
                    });

            // Mật khẩu dummy cho tài khoản OAuth (không có mật khẩu thật)
            // Có thể dùng một UUID ngẫu nhiên và mã hóa nó, hoặc một giá trị đặc biệt
            String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            User newUser = User.builder()
                    .userPublicId(UUID.randomUUID().toString())
                    .userName(userName) // Email thường là userName cho tài khoản OAuth
                    .password(dummyPassword) // Mật khẩu được tạo ngẫu nhiên hoặc null
                    .fullName(fullName)
                    .email(email)
                    .phone(null) // Số điện thoại thường không có từ Google
                    .registrationDate(LocalDateTime.now())
                    .role(defaultRole)
                    .status("active")
                    .isEmailVerified(true) // Email đã được Google xác thực
                    .authProvider("GOOGLE") // Đánh dấu là từ Google
                    .providerId(googleId) // Lưu Google ID
                    .build();

            return userRepository.save(newUser);
        }
    }

}
