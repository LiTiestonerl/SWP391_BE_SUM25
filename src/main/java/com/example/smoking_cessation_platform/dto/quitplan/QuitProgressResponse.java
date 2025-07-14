package com.example.smoking_cessation_platform.dto.quitplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitProgressResponse {
    private Integer progressId;
    private LocalDate date;
    private Integer cigarettesSmoked;
    private BigDecimal moneySpent;
    private BigDecimal moneySaved;
    private Integer smokingFreeDays;
    private String healthStatus;
}
