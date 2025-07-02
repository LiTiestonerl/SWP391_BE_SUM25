package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.stereotype.Component;

@Component
public class CigarettePackageMapper {

    public CigarettePackageDTO toDTO(CigarettePackage pkg) {
        if (pkg == null) return null;

        return CigarettePackageDTO.builder()
                .cigaretteId(pkg.getCigaretteId())
                .cigaretteName(pkg.getCigaretteName())
                .price(pkg.getPrice())
                .sticksPerPack(pkg.getSticksPerPack())
                .build();
    }

    public CigarettePackage toEntity(CigarettePackageDTO dto) {
        if (dto == null) return null;

        return CigarettePackage.builder()
                .cigaretteId(dto.getCigaretteId()) // Chỉ dùng nếu update
                .cigaretteName(dto.getCigaretteName())
                .price(dto.getPrice())
                .sticksPerPack(dto.getSticksPerPack())
                .build();
    }
}