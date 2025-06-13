package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.auth.*;
import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.entity.Users;
import com.example.smoking_cessation_platform.entity.EmailVerificationToken;
import com.example.smoking_cessation_platform.repository.EmailVerificationTokenRepository;
import com.example.smoking_cessation_platform.repository.RoleRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private final Random random = new Random();

    @Transactional
    public Users registerUser(RegisterRequest request) {
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

        Users newUser = Users.builder()
                .userPublicId(userPublicId)
                .userName(request.getUserName())
                .password(encodedPassword)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .registrationDate(LocalDateTime.now())
                .role(defaultRole)
                .status("active")
                .is2FaEnabled(false)
                .twoFactorSecret(null)
                .isEmailVerified(false)
                .build();

        Users savedUser = userRepository.save(newUser);

        sendEmailVerificationOtp(savedUser);

        return savedUser;
    }

    /**
     * Gửi mã OTP xác thực email cho người dùng.
     * @param user Người dùng cần gửi mã OTP.
     */
    @Transactional
    public void sendEmailVerificationOtp(Users user) {
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
     * @param request EmailVerificationRequest chứa email của người dùng.
     */
    @Transactional
    public void resendEmailVerificationOtp(EmailVerificationRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));

        if (user.getIsEmailVerified()) {
            throw new RuntimeException("Email này đã được xác thực.");
        }

//         Tùy chọn: Kiểm tra nếu có OTP còn hiệu lực gần đây, không gửi lại ngay.
         Optional<EmailVerificationToken> latestToken = emailVerificationTokenRepository
             .findFirstByUserAndConfirmedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(user, LocalDateTime.now());
         if (latestToken.isPresent()) {
             // Nếu có token chưa hết hạn, có thể throw lỗi hoặc yêu cầu chờ
             throw new RuntimeException("Mã OTP đã được gửi gần đây. Vui lòng kiểm tra email hoặc chờ.");
         }

        sendEmailVerificationOtp(user); // Gửi lại mã OTP
    }


    /**
     * Xác minh mã OTP được gửi qua email.
     * Nếu mã hợp lệ và chưa hết hạn, email sẽ được đánh dấu là đã xác thực.
     * @param request VerifyEmailOtpRequest chứa email và mã OTP.
     * @return true nếu xác minh thành công, ngược lại false.
     */
    @Transactional
    public boolean verifyEmailOtp(VerifyEmailOtpRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
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

    @Transactional
    public TwoFactorAuthSetupResponse generate2FaSetup(String usernameOrEmail) {
        Users user = userRepository.findByUserName(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (user.getIs2FaEnabled() != null && user.getIs2FaEnabled()) {
            // Tùy chọn: thông báo 2FA đã kích hoạt, hoặc reset secret
        }

        GoogleAuthenticatorKey newKey = gAuth.createCredentials();
        String secretKey = newKey.getKey();

        user.setTwoFactorSecret(secretKey);
        userRepository.save(user);

        String accountName = user.getEmail() != null ? user.getEmail() : user.getUserName();
        String issuerName = "SmokingCessationPlatform";

        String qrCodeUrl = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuerName, accountName, secretKey, issuerName);

        return TwoFactorAuthSetupResponse.builder()
                .qrCodeImageUrl(qrCodeUrl)
                .secretKey(secretKey)
                .message("Sử dụng ứng dụng Authenticator để quét mã QR hoặc nhập khóa bí mật.")
                .build();
    }

    @Transactional
    public boolean verifyAndActivate2Fa(TwoFactorAuthVerifyRequest request) {
        Users user = userRepository.findByUserName(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isEmpty()) {
            throw new RuntimeException("Người dùng chưa thiết lập khóa bí mật 2FA. Vui lòng tạo khóa trước.");
        }

        boolean isOtpValid = gAuth.authorize(user.getTwoFactorSecret(), Integer.parseInt(request.getOtp()));

        if (isOtpValid) {
            user.setIs2FaEnabled(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean verify2FaOnly(String usernameOrEmail, String otp) {
        Users user = userRepository.findByUserName(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isEmpty() || !user.getIs2FaEnabled()) {
            return true;
        }

        return gAuth.authorize(user.getTwoFactorSecret(), Integer.parseInt(otp));
    }

    @Transactional
    public boolean disable2Fa(String usernameOrEmail) {
        Users user = userRepository.findByUserName(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (user.getIs2FaEnabled() != null && user.getIs2FaEnabled()) {
            user.setIs2FaEnabled(false);
            user.setTwoFactorSecret(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
