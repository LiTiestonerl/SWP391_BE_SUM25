package com.example.smoking_cessation_platform.dto.achievementbadge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementBadgeRequest {
    private String badgeName;
    private String description;
    private String criteria;
    private String badgeType;
}
