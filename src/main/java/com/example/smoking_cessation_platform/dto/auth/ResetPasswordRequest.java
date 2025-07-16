package com.example.smoking_cessation_platform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không hợp lệ.")
    private String email;
    @NotBlank(message = "Mã OTP không được để trống.")
    private String otp;
    @NotBlank(message = "Mật khẩu mới không được để trống.")
    private String newPassword;
}
