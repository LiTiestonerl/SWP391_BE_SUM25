package com.example.smoking_cessation_platform.dto.smokingstatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmokingStatusResponse {
    private Integer statusId;
    private Integer cigarettesPerDay;
    private String frequency;
    private Long packageId;
    private String packageName;
    private BigDecimal pricePerPack;
    private LocalDate recordDate;

    private Long userId;

    private Long cigarettePackageId;
    private String cigarettePackageName;


}
