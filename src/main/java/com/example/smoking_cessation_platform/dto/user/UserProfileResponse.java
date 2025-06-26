package com.example.smoking_cessation_platform.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long userId;

    private String userPublicId;

    private String userName;

    private String fullName;

    private String email;

    private String phone;

    private LocalDateTime registrationDate;

    private String status;

    private String roleName;

    private Boolean isEmailVerified;

    private String authProvider;
}
