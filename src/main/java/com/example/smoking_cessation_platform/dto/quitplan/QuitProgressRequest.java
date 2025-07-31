package com.example.smoking_cessation_platform.dto.quitplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitProgressRequest {
    private LocalDate date;

    private Integer cigarettesSmoked;

    private Integer smokingFreeDays;

    private String healthStatus;

    private Integer stageId; // ID của QuitPlanStage
}
