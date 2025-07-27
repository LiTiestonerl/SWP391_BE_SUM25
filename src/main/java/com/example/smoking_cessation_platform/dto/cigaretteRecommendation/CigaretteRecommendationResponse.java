package com.example.smoking_cessation_platform.dto.cigaretteRecommendation;

import com.example.smoking_cessation_platform.enums.NicotineStrength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CigaretteRecommendationResponse {

    private Integer recId;
    private String notes;
    private Integer priorityOrder;
    private Boolean isActive;

    // From Package details
    private Long fromPackageId;
    private String fromCigaretteName;
    private String fromBrand;
    private NicotineStrength fromNicoteneStrength;
    private String fromFlavor;
    private BigDecimal fromPrice;
    private Integer fromSticksPerPack;

    // To Package details
    private Long toPackageId;
    private String toCigaretteName;
    private String toBrand;
    private NicotineStrength toNicoteneStrength;
    private String toFlavor;
    private BigDecimal toPrice;
    private Integer toSticksPerPack;

    // Smoking Status details
    private Integer smokingStatusId;
    private String smokingStatusDescription;
}
