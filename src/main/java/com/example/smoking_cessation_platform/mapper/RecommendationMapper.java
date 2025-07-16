package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {
    /**
     * Chuyển đổi từ entity CigaretteRecommendation sang DTO RecommendationResponse
     * @param rec Entity CigaretteRecommendation
     * @return RecommendationResponse DTO, hoặc null nếu entity null hoặc toPackage null
     */
    public static CigarettePackageDTO toPackageDTO(CigaretteRecommendation entity) {
        CigarettePackage to = entity.getToPackage();
        if (to == null) return null;

        CigarettePackageDTO dto = new CigarettePackageDTO();
        dto.setCigaretteId(to.getCigaretteId());
        dto.setCigaretteName(to.getCigaretteName());
        dto.setPrice(to.getPrice());
        dto.setBrand(to.getBrand());
        dto.setNicoteneStrength(to.getNicoteneStrength());
        dto.setFlavor(to.getFlavor());
        dto.setSticksPerPack(to.getSticksPerPack());
        dto.setNicotineMg(to.getNicotineMg());
        return dto;
    }
}
