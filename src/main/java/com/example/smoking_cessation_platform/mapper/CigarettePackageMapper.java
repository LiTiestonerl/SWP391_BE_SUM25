package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.stereotype.Component;
@Component
public class CigarettePackageMapper {

    // ✅ Chuyển từ entity -> DTO (trả về cho client)
    public  CigarettePackageDTO toDTO(CigarettePackage entity) {
        if (entity == null) return null;
        return CigarettePackageDTO.builder()
                .cigaretteId(entity.getCigaretteId())
                .cigaretteName(entity.getCigaretteName())
                .price(entity.getPrice())
                .brand(entity.getBrand())
                .nicoteneStrength(entity.getNicoteneStrength())
                .flavor(entity.getFlavor())
                .sticksPerPack(entity.getSticksPerPack())
                .nicotineMg(entity.getNicotineMg())
                .build();
    }

    public  CigarettePackage toEntity(CigarettePackageDTO dto) {
        if (dto == null) return null;
        return CigarettePackage.builder()
                .cigaretteId(dto.getCigaretteId()) // Có thể bỏ dòng này nếu dùng để tạo mới
                .cigaretteName(dto.getCigaretteName())
                .price(dto.getPrice())
                .brand(dto.getBrand())
                .nicoteneStrength(dto.getNicoteneStrength())
                .flavor(dto.getFlavor())
                .sticksPerPack(dto.getSticksPerPack())
                .nicotineMg(dto.getNicotineMg())
                .build();
    }
}