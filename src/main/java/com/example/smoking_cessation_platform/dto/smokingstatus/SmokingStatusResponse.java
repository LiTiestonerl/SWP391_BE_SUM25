package com.example.smoking_cessation_platform.dto.smokingstatus;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String preferredFlavor;
    private String preferredNicotineLevel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private Long userId;

    private Long cigarettePackageId;
    private String cigarettePackageName;


}
