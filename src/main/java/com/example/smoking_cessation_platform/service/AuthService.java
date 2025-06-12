package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.entity.Users; // Đã sửa từ Users sang User
import com.example.smoking_cessation_platform.dto.auth.RegisterRequest;
import com.example.smoking_cessation_platform.dto.auth.TwoFactorAuthSetupResponse;
import com.example.smoking_cessation_platform.dto.auth.TwoFactorAuthVerifyRequest;
import com.example.smoking_cessation_platform.repository.RoleRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator; // Import thư viện
import com.warrenstrange.googleauth.GoogleAuthenticatorKey; // Import thư viện
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Khởi tạo GoogleAuthenticator
    // gAuth sẽ dùng cài đặt mặc định (6 chữ số, thời gian hiệu lực 30 giây)
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Transactional // Đảm bảo toàn bộ hoạt động là một transaction
    public Users registerUser(RegisterRequest request) { // Đã sửa kiểu trả về từ Users sang User
        // 1. Kiểm tra tồn tại Email hoặc Username
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng"); // Hoặc một custom exception
        }
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("Tên người dùng đã được sử dụng"); // Hoặc một custom exception
        }

        // 2. Tìm hoặc tạo Role mặc định (ví dụ: "USER")
        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    // Nếu role "USER" chưa tồn tại, tạo mới
                    Role newRole = new Role();
                    newRole.setRoleName("USER");
                    newRole.setDescription("Người dùng thông thường");
                    return roleRepository.save(newRole);
                });

        // 3. Tạo User Public ID duy nhất
        String userPublicId = UUID.randomUUID().toString();

        // 4. Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 5. Chuyển đổi DTO sang Entity User
        Users newUser = Users.builder() // Đã sửa từ Users.builder() sang User.builder()
                .userPublicId(userPublicId)
                .userName(request.getUserName())
                .password(encodedPassword) // Lưu mật khẩu đã mã hóa
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .registrationDate(LocalDateTime.now())
                .role(defaultRole) // Gán role mặc định
                .status("active")
                .is2FaEnabled(false) // Mặc định 2FA chưa kích hoạt khi đăng ký
                .twoFactorSecret(null) // Chưa có secret key khi đăng ký
                .build();

        // 6. Lưu User vào cơ sở dữ liệu
        return userRepository.save(newUser);
    }

    /**
     * Phương thức này tạo ra một khóa bí mật 2FA mới và trả về thông tin để người dùng thiết lập.
     * Khóa bí mật này sẽ được lưu tạm thời vào DB.
     * 2FA chỉ được kích hoạt SAU KHI người dùng xác minh mã OTP thành công.
     *
     * @param usernameOrEmail Tên người dùng hoặc email của người dùng.
     * @return TwoFactorAuthSetupResponse chứa URL mã QR và khóa bí mật.
     */
    @Transactional
    public TwoFactorAuthSetupResponse generate2FaSetup(String usernameOrEmail) {
        // Tìm người dùng theo username hoặc email
        Users user = userRepository.findByUserName(usernameOrEmail) // Đã sửa từ Users sang User
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        // Tạo một khóa bí mật mới
        GoogleAuthenticatorKey newKey = gAuth.createCredentials(); // Đã sửa lỗi cú pháp
        String secretKey = newKey.getKey(); // Đây là chuỗi khóa bí mật mà người dùng cần để thiết lập

        // Lưu khóa bí mật vào người dùng (tạm thời, is2FaEnabled vẫn là false)
        user.setTwoFactorSecret(secretKey);
        userRepository.save(user);

        // Tạo URL cho mã QR (Google Authenticator định dạng: otpauth://totp/ACCOUNT_NAME?secret=SECRET_KEY&issuer=ISSUER_NAME)
        // ACCOUNT_NAME thường là email của người dùng hoặc username.
        // ISSUER_NAME là tên ứng dụng của bạn (ví dụ: SmokingCessationPlatform)
        String accountName = user.getEmail() != null ? user.getEmail() : user.getUserName();
        String issuerName = "SmokingCessationPlatform"; // Tên ứng dụng của bạn

        String qrCodeUrl = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuerName, accountName, secretKey, issuerName);

        // Trả về response cho frontend
        return TwoFactorAuthSetupResponse.builder()
                .qrCodeImageUrl(qrCodeUrl)
                .secretKey(secretKey)
                .message("Sử dụng ứng dụng Authenticator để quét mã QR hoặc nhập khóa bí mật.")
                .build();
    }

    /**
     * Phương thức này xác minh mã OTP do người dùng cung cấp.
     * Nếu mã hợp lệ, 2FA sẽ được kích hoạt cho người dùng đó.
     *
     * @param request TwoFactorAuthVerifyRequest chứa OTP và identifier (username/email).
     * @return true nếu xác minh thành công và 2FA được kích hoạt, ngược lại false.
     */
    @Transactional
    public boolean verifyAndActivate2Fa(TwoFactorAuthVerifyRequest request) {
        // Tìm người dùng
        Users user = userRepository.findByUserName(request.getIdentifier()) // Đã sửa từ Users sang User
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        // Kiểm tra xem người dùng có khóa bí mật 2FA chưa
        if (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isEmpty()) {
            throw new RuntimeException("Người dùng chưa thiết lập khóa bí mật 2FA. Vui lòng tạo khóa trước.");
        }

        // Xác minh mã OTP
        // Hàm authorize sẽ kiểm tra OTP hiện tại và các OTP trong cửa sổ thời gian gần (trước và sau)
        // Cửa sổ mặc định của GoogleAuthenticator là 1 (nghĩa là kiểm tra mã hiện tại, mã trước đó và mã sau đó)
        boolean isOtpValid = gAuth.authorize(user.getTwoFactorSecret(), Integer.parseInt(request.getOtp()));

        if (isOtpValid) {
            // Nếu OTP hợp lệ, kích hoạt 2FA cho người dùng
            user.setIs2FaEnabled(true);
            userRepository.save(user);
            return true;
        } else {
            // Nếu OTP không hợp lệ, không kích hoạt
            return false;
        }
    }

    /**
     * Phương thức này xác minh mã OTP mà không kích hoạt/tắt 2FA.
     * Dùng cho quá trình đăng nhập (sẽ được tích hợp sau)
     *
     * @param usernameOrEmail Tên người dùng hoặc email.
     * @param otp Mã OTP từ người dùng.
     * @return true nếu mã OTP hợp lệ, ngược lại false.
     */
    public boolean verify2FaOnly(String usernameOrEmail, String otp) {
        Users user = userRepository.findByUserName(usernameOrEmail) // Đã sửa từ Users sang User
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isEmpty() || !user.getIs2FaEnabled()) {
            // Nếu 2FA không được bật hoặc không có secret, không cần xác minh 2FA
            return true; // Hoặc throw exception nếu bạn muốn bắt buộc phải có 2FA
        }

        return gAuth.authorize(user.getTwoFactorSecret(), Integer.parseInt(otp));
    }

    /**
     * Phương thức để tắt 2FA (nếu cần cho chức năng quản lý tài khoản)
     * Thường yêu cầu xác minh mật khẩu hoặc OTP hiện tại trước khi tắt.
     * @param usernameOrEmail
     * @return
     */
    @Transactional
    public boolean disable2Fa(String usernameOrEmail) {
        Users user = userRepository.findByUserName(usernameOrEmail) // Đã sửa từ Users sang User
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (user.getIs2FaEnabled() != null && user.getIs2FaEnabled()) {
            user.setIs2FaEnabled(false);
            user.setTwoFactorSecret(null); // Xóa secret key để bảo mật
            userRepository.save(user);
            return true;
        }
        return false; // 2FA đã không được kích hoạt
    }
}
