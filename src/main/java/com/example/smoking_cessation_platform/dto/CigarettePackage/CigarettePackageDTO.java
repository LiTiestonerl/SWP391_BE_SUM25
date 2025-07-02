package com.example.smoking_cessation_platform.dto.CigarettePackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CigarettePackageDTO {
    private Long cigaretteId;
    private String cigaretteName;
    private BigDecimal price;
    private Integer sticksPerPack;
}
