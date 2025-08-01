package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.cigaretteRecommendation.CigaretteRecommendationRequest;
import com.example.smoking_cessation_platform.dto.cigaretteRecommendation.CigaretteRecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import com.example.smoking_cessation_platform.entity.SmokingStatus;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import com.example.smoking_cessation_platform.repository.CigaretteRecommendationRepository;
import com.example.smoking_cessation_platform.repository.SmokingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CigaretteRecommendationService {

    @Autowired
    private CigaretteRecommendationRepository recommendationRepository;

    @Autowired
    private CigarettePackageRepository packageRepository;

    @Autowired
    private SmokingStatusRepository smokingStatusRepository;

    /**
     * Lấy gợi ý thuốc lá nhẹ hơn (giảm nicotine)
     */
    public List<CigaretteRecommendationResponse> getLighterNicotineRecommendations(Long cigaretteId) {
        List<CigaretteRecommendation> recommendations = recommendationRepository
                .findLighterNicotineRecommendations(cigaretteId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy gợi ý thuốc lá cùng hương vị
     */
    public List<CigaretteRecommendationResponse> getSameFlavorRecommendations(Long cigaretteId) {
        List<CigaretteRecommendation> recommendations = recommendationRepository
                .findSameFlavorRecommendations(cigaretteId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy gợi ý thuốc lá cùng thương hiệu nhưng nhẹ hơn
     */
    public List<CigaretteRecommendationResponse> getSameBrandLighterRecommendations(Long cigaretteId) {
        List<CigaretteRecommendation> recommendations = recommendationRepository
                .findSameBrandLighterRecommendations(cigaretteId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy gợi ý thuốc lá cùng thương hiệu, cùng hương vị nhưng nhẹ hơn (tốt nhất)
     */
    public List<CigaretteRecommendationResponse> getBestRecommendations(Long cigaretteId) {
        List<CigaretteRecommendation> recommendations = recommendationRepository
                .findSameBrandFlavorLighterRecommendations(cigaretteId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả gợi ý cho một loại thuốc (kết hợp nhiều chiến lược)
     */
    public List<CigaretteRecommendationResponse> getRecommendationsByFromPackage(Long fromPackageId) {
        List<CigaretteRecommendation> recommendations = recommendationRepository
                .findByFromPackage_CigaretteIdAndIsActiveTrue(fromPackageId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy gợi ý theo trạng thái hút thuốc
     */
    public List<CigaretteRecommendationResponse> getRecommendationsBySmokingStatus(Integer smokingStatusId) {
        List<CigaretteRecommendation> recommendations = recommendationRepository.findBySmokingStatus_StatusIdAndIsActiveTrue(smokingStatusId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả recommendations đang active
     */
    public List<CigaretteRecommendationResponse> getAllActiveRecommendations() {
        List<CigaretteRecommendation> recommendations = recommendationRepository
                .findAll().stream()
                .filter(r -> r.getIsActive())
                .collect(Collectors.toList());
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<CigaretteRecommendationResponse> getRecommendationById(Integer recId) {
        return recommendationRepository.findById(recId)
                .map(this::convertToResponse);
    }

    /**
     * Admin: Toggle active status
     */
    public CigaretteRecommendationResponse toggleActiveStatus(Integer recId) {
        CigaretteRecommendation existing = recommendationRepository.findById(recId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        existing.setIsActive(!existing.getIsActive());
        CigaretteRecommendation updated = recommendationRepository.save(existing);
        return convertToResponse(updated);
    }

    /**
     * Admin: Update priority
     */
    public CigaretteRecommendationResponse updatePriority(Integer recId, Integer priority) {
        CigaretteRecommendation existing = recommendationRepository.findById(recId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        existing.setPriorityOrder(priority);
        CigaretteRecommendation updated = recommendationRepository.save(existing);
        return convertToResponse(updated);
    }

    private CigaretteRecommendationResponse convertToResponse(CigaretteRecommendation recommendation) {
        return CigaretteRecommendationResponse.builder()
                .recId(recommendation.getRecId())
                .notes(recommendation.getNotes())
                .priorityOrder(recommendation.getPriorityOrder())
                .isActive(recommendation.getIsActive())
                .fromPackageId(recommendation.getFromPackage().getCigaretteId())
                .fromCigaretteName(recommendation.getFromPackage().getCigaretteName())
                .fromBrand(recommendation.getFromPackage().getBrand())
                .fromNicoteneStrength(recommendation.getFromPackage().getNicoteneStrength())
                .fromFlavor(recommendation.getFromPackage().getFlavor())
                .fromPrice(recommendation.getFromPackage().getPrice())
                .fromSticksPerPack(recommendation.getFromPackage().getSticksPerPack())
                .toPackageId(recommendation.getToPackage().getCigaretteId())
                .toCigaretteName(recommendation.getToPackage().getCigaretteName())
                .toBrand(recommendation.getToPackage().getBrand())
                .toNicoteneStrength(recommendation.getToPackage().getNicoteneStrength())
                .toFlavor(recommendation.getToPackage().getFlavor())
                .toPrice(recommendation.getToPackage().getPrice())
                .toSticksPerPack(recommendation.getToPackage().getSticksPerPack())
                .smokingStatusId(recommendation.getSmokingStatus() != null ?
                        recommendation.getSmokingStatus().getStatusId() : null)
                .smokingStatusDescription(recommendation.getSmokingStatus() != null ?
                        "Smoking Status " + recommendation.getSmokingStatus().getStatusId() : null)
                .build();
    }
}