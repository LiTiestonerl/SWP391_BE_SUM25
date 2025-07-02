package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    public RecommendationResponse toDTO(CigaretteRecommendation rec) {
        if (rec == null) return null;

        return RecommendationResponse.builder()
                .recId(rec.getRecId())
                .fromPackageId(rec.getFromPackage().getCigaretteId())
                .toPackageId(rec.getToPackage().getCigaretteId())
                .notes(rec.getNotes())
                .build();
    }
}
