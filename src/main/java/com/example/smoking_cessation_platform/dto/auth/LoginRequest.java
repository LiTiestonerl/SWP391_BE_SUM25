package com.example.smoking_cessation_platform.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Pattern(
            regexp = "^(?:[A-Za-z0-9._%+-]{3,50}@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}|[A-Za-z0-9_.-]{3,50})$",
            message = "Tên đăng nhập phải là email hợp lệ hoặc username 3-50 ký tự"
    )
    @Schema(example = "user@gmail.com") // ✅ Ví dụ email hoặc username
    private String login;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Schema(example = "123456")         // ✅ Ví dụ password
    private String password;
}
