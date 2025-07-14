package com.example.smoking_cessation_platform.controller;


import com.example.smoking_cessation_platform.dto.auth.*;

import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.service.AuthService;
import com.example.smoking_cessation_platform.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User registeredUser = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký tài khoản thành công cho: " + registeredUser.getUserName() + ". Vui lòng kiểm tra email để xác thực tài khoản.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký tài khoản.");
        }
    }

    /**
     * API để yêu cầu gửi lại mã OTP xác thực email.
     * Người dùng cung cấp email.
     */
    @PostMapping("/email/resend-otp")
    public ResponseEntity<?> resendEmailOtp(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            authService.resendEmailVerificationOtp(request);
            return ResponseEntity.ok("Mã OTP đã được gửi lại thành công đến email: " + request.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi gửi lại mã OTP email.");
        }
    }

    /**
     * API để xác minh mã OTP email.
     * Người dùng cung cấp email và mã OTP.
     */
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequest request) {
        try {
            boolean verified = authService.verifyEmailOtp(request);
            if (verified) {
                return ResponseEntity.ok("Email đã được xác thực thành công.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xác minh OTP email.");
        }
    }

    /**
     * API đăng ký & đăng nhập Google OAuth.
     */
    @Operation(summary = "Đăng nhập hoặc đăng ký bằng Google OAuth2")
    @PostMapping("/google")
    public ResponseEntity<?> registerOrLoginWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            User user = authService.registerOrLoginWithGoogle(request);
            return ResponseEntity.ok("Đăng nhập/Đăng ký Google thành công cho: " + user.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng nhập/đăng ký bằng Google.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);                         // 200 OK + token
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)            // 400 Bad Request
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
                    .body("Đã xảy ra lỗi khi đăng nhập.");
        }

    }
}
