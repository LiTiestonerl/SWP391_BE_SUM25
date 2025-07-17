package com.example.smoking_cessation_platform.dto.dashboard;

import com.example.smoking_cessation_platform.dto.dashboard.setup.TopBadge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BadgeStatsResponse {
    private long totalBadgesAwarded;       // Tổng số huy hiệu đã trao
    private List<TopBadge> topBadges;      // Top huy hiệu phổ biến
}