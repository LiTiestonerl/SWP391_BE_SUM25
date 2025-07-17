package com.example.smoking_cessation_platform.dto.dashboard.setup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopBadge {
    private Integer badgeId;       // ID huy hiệu
    private String badgeName;   // Tên huy hiệu
    private long awardedCount;  // Số lần được trao
}