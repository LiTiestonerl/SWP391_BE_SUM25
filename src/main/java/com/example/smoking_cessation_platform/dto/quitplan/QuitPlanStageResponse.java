package com.example.smoking_cessation_platform.dto.quitplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanStageResponse {
    private Integer stageId;
    private String stageName;
    private LocalDate stageStartDate;
    private LocalDate stageEndDate;
    private Integer targetCigarettesPerDay;
    private String notes;
    private Set<QuitProgressResponse> quitProgresses; // Danh sách tiến trình
}
