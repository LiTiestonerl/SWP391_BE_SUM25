package com.example.smoking_cessation_platform.service;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import com.example.smoking_cessation_platform.mapper.RecommendationMapper;
import com.example.smoking_cessation_platform.repository.CigaretteRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CigaretteRecommendationService {

    @Autowired
    CigaretteRecommendationRepository cigaretteRecommendationRepository;

    @Autowired
    CigarettePackageService cigarettePackageService;

    @Autowired
    RecommendationMapper recommendationMapper;

    public List<RecommendationResponse> getRecommendationsFrom(Long fromPackageId) {
        // 1. Lấy gói thuốc gốc từ ID
        CigarettePackage from = cigarettePackageService.getPackageId(fromPackageId);

        // 2. Tìm tất cả các recommendation xuất phát từ gói đó
        List<CigaretteRecommendation> recommendations = cigaretteRecommendationRepository.findByFromPackage(from);

        // 3. Chuyển sang DTO (RecommendationResponse) để trả ra client
        return recommendations.stream()
                .map(recommendationMapper::toDTO)
                .collect(Collectors.toList());
    }
}
