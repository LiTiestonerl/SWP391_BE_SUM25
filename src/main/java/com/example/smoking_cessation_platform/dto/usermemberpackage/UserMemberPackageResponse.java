package com.example.smoking_cessation_platform.dto.usermemberpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMemberPackageResponse {
    private Integer userMemberPackageId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer userId;           // lấy từ user.getUserId()
    private Integer memberPackageId; // lấy từ memberPackage.getMemberPackageId()
}