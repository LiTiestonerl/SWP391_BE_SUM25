package com.example.smoking_cessation_platform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {
    @NotBlank(message = "Google ID Token không được để trống")
    private String idToken;
}
