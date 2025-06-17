package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.EmailVerificationToken;
import com.example.smoking_cessation_platform.dto.auth.RegisterRequest;
import com.example.smoking_cessation_platform.dto.auth.EmailVerificationRequest;
import com.example.smoking_cessation_platform.dto.auth.VerifyEmailOtpRequest;
import com.example.smoking_cessation_platform.repository.EmailVerificationTokenRepository;
import com.example.smoking_cessation_platform.repository.RoleRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
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

    private final Random random = new Random();

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

}
