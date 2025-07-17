package com.example.smoking_cessation_platform.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String userPublicId;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private String token;
    private String tokenType = "Bearer"; // ✅ thêm field này
    private String refreshToken; // 🔥 Thêm dòng này
}
