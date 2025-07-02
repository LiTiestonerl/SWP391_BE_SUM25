package com.example.smoking_cessation_platform.dto.CigarettePackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequest {
    private Long fromPackageId;
    private Long toPackageId;
    private String notes;
}
