package com.example.smoking_cessation_platform.dto.cigarettepackage;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CigarettePackageResponse {

    private Long cigarettePackageId;

    private String cigarettePackageName;

    private String brand;

    private String flavor;

    private String nicotineLevel;

    private Integer sticksPerPack;

    private BigDecimal pricePerPack;

}
