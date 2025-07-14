package com.example.smoking_cessation_platform.dto.userbadge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBadgeResponse {
    private Integer userBadgeId;      // ID của bản ghi user-badge
    private LocalDate dateAchieved;   // Ngày đạt huy hiệu
    private Boolean shared;           // Huy hiệu có được chia sẻ không (nếu có)

    // Thông tin người dùng
    private Long  userId;
    private String username;

    // Thông tin huy hiệu
    private Integer badgeId;
    private String badgeName;
    private String badgeDescription;
    private String badgeCriteria;
}
