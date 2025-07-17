package com.example.smoking_cessation_platform.dto.dashboard.setup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyCount {
    private String month; // ví dụ "2025-07"
    private long count;
}