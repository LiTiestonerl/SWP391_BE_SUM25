package com.example.smoking_cessation_platform.dto.cigaretteRecommendation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CigaretteRecommendationRequest {

    private Long fromPackageId;
    private Long toPackageId;
    private String notes;
    private Integer priorityOrder;
    private Boolean isActive;
    private Integer smokingStatusId;

}
