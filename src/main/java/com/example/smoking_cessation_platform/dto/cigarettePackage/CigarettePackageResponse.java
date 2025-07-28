package com.example.smoking_cessation_platform.dto.CigarettePackage;

import com.example.smoking_cessation_platform.Enum.NicotineStrength;
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

    private NicotineStrength nicotineLevel;

    private Integer sticksPerPack;

    private BigDecimal pricePerPack;

}
