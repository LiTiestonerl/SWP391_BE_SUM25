package com.example.smoking_cessation_platform.dto.dashboard;

import com.example.smoking_cessation_platform.dto.dashboard.setup.MonthlyCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private Map<String, Long> userByRole; // ví dụ {"USER":100, "COACH":10}
    private List<MonthlyCount> newUsersByMonth;
}
