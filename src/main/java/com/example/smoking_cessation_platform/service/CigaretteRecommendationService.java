package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import com.example.smoking_cessation_platform.mapper.RecommendationMapper;
import com.example.smoking_cessation_platform.repository.CigaretteRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CigaretteRecommendationService {

    @Autowired
    private CigaretteRecommendationRepository cigaretteRecommendationRepository;

    @Autowired
    private CigarettePackageService cigarettePackageService;

    @Autowired
    private RecommendationMapper recommendationMapper;

    /**
     * Lấy danh sách gợi ý cho 1 gói thuốc cụ thể.
     */
    public List<RecommendationResponse> getRecommendationsFrom(Long fromPackageId) {
        CigarettePackage from = cigarettePackageService.getPackageId(fromPackageId); // lấy entity
        List<CigaretteRecommendation> recs = cigaretteRecommendationRepository.findByFromPackage(from);

        return recs.stream()
                .map(recommendationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy 1 số gói thuốc có nicotine thấp hơn gói hiện tại → dùng khi tạo QuitPlan
     */
    public List<CigarettePackageDTO> suggestLowerNicotinePackages(Long currentPackageId) {
        CigarettePackage current = cigarettePackageService.getPackageId(currentPackageId);
        List<CigarettePackage> all = cigarettePackageService.getAllPackages(); // bạn có thể cache nếu cần

        return all.stream()
                .filter(p -> p.getNicotineMg() != null && p.getNicotineMg() < current.getNicotineMg())
                .sorted(Comparator.comparing(CigarettePackage::getNicotineMg))
                .limit(5)
                .map(p -> CigarettePackageDTO.builder()
                        .cigaretteId(p.getCigaretteId())
                        .cigaretteName(p.getCigaretteName())
                        .price(p.getPrice())
                        .sticksPerPack(p.getSticksPerPack())
                        .build())
                .collect(Collectors.toList());
    }
}
