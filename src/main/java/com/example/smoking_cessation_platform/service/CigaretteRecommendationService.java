package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CigaretteRecommendationService {

    @Autowired
    private CigarettePackageService cigarettePackageService;

    /**
     * Lấy danh sách gợi ý cho 1 gói thuốc cụ thể – các gói có nicotine thấp hơn.
     */
    public List<RecommendationResponse> getRecommendationsFrom(Long fromPackageId) {
        CigarettePackage current = cigarettePackageService.getPackageId(fromPackageId);
        List<CigarettePackage> all = cigarettePackageService.getAllPackages();

        return all.stream()
                .filter(p -> p.getNicotineMg() != null && p.getNicotineMg() < current.getNicotineMg())
                .sorted(Comparator.comparing(CigarettePackage::getNicotineMg))
                .limit(5)
                .map(p -> RecommendationResponse.builder()
                        .recId(null)                // Không có ID bản ghi recommendation
                        .fromPackageId(fromPackageId)   // Gói gốc
                        .toPackageId(p.getCigaretteId()) // Gói gợi ý
                        .notes(null)                // Không có ghi chú
                        .toPackageDetail(CigarettePackageDTO.builder()
                                .cigaretteId(p.getCigaretteId())
                                .cigaretteName(p.getCigaretteName())
                                .price(p.getPrice())
                                .brand(p.getBrand())
                                .nicoteneStrength(p.getNicoteneStrength())
                                .flavor(p.getFlavor())
                                .sticksPerPack(p.getSticksPerPack())
                                .nicotineMg(p.getNicotineMg())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }
}
