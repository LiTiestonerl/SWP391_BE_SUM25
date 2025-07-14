package com.example.smoking_cessation_platform.dto.CigarettePackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
    private Integer recId;
    private Long fromPackageId;
    private Long toPackageId;
    private String notes;

    private CigarettePackageDTO toPackageDetail;
}
