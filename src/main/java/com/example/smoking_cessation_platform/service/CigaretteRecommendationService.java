package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import com.example.smoking_cessation_platform.repository.CigaretteRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CigaretteRecommendationService {

    @Autowired
    private CigarettePackageRepository cigarettePackageRepository;

    /**
     * Lấy danh sách gợi ý cho 1 gói thuốc cụ thể – các gói có nicotine thấp hơn.
     */
    public List<RecommendationResponse> getRecommendationsFrom(Long fromPackageId) {
        CigarettePackage current = cigarettePackageRepository.findById(fromPackageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói thuốc"));

        return cigarettePackageRepository.findAll().stream()
                .filter(pkg -> pkg.getNicotineMg() != null
                        && current.getNicotineMg() != null
                        && pkg.getNicotineMg() < current.getNicotineMg())
                .sorted(Comparator.comparing(CigarettePackage::getNicotineMg))
                .limit(5)
                .map(pkg -> RecommendationResponse.builder()
                        .recId(null) // vì không lưu DB
                        .fromPackageId(fromPackageId)
                        .toPackageId(pkg.getCigaretteId())
                        .notes(null)
                        .toPackageDetail(CigarettePackageDTO.builder()
                                .cigaretteId(pkg.getCigaretteId())
                                .cigaretteName(pkg.getCigaretteName())
                                .price(pkg.getPrice())
                                .brand(pkg.getBrand())
                                .nicoteneStrength(pkg.getNicoteneStrength())
                                .flavor(pkg.getFlavor())
                                .sticksPerPack(pkg.getSticksPerPack())
                                .nicotineMg(pkg.getNicotineMg())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }
}
