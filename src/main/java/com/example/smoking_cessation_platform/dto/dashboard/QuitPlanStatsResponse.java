package com.example.smoking_cessation_platform.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanStatsResponse {
    private long totalPlans;       // Tổng số kế hoạch
    private long activePlans;      // Số kế hoạch đang ACTIVE
    private long completedPlans;   // Số kế hoạch COMPLETED
    private long canceledPlans;    // Số kế hoạch CANCELED
    private double completionRate; // Tỷ lệ hoàn thành (%)
}