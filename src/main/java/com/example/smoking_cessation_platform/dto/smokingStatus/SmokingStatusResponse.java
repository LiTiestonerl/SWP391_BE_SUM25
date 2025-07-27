package com.example.smoking_cessation_platform.dto.smokingStatus;

import com.example.smoking_cessation_platform.enums.NicotineStrength;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmokingStatusResponse {
    private Integer statusId;
    private Integer cigarettesPerDay;
    private String frequency;
    private String preferredFlavor;
    private NicotineStrength preferredNicotineLevel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private Long userId;

    private Long cigarettePackageId;
    private String cigarettePackageName;


}
