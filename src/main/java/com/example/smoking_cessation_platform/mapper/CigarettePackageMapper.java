package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.stereotype.Component;
@Component
public class CigarettePackageMapper {

    // ✅ Chuyển từ entity -> DTO (trả về cho client)
    public CigarettePackageDTO toDTO(CigarettePackage pkg) {
        if (pkg == null) return null; // Tránh lỗi null

        return CigarettePackageDTO.builder()
                .cigaretteId(pkg.getCigaretteId())          // Gán ID
                .cigaretteName(pkg.getCigaretteName())      // Gán tên
                .price(pkg.getPrice())                      // Gán giá
                .sticksPerPack(pkg.getSticksPerPack())      // Gán số điếu
                .nicotineMg(pkg.getNicotineMg())            // ✅ Gán hàm lượng nicotine (nếu có)
                .build();
    }

    // ✅ Chuyển từ DTO -> entity (dùng khi tạo/cập nhật)
    public CigarettePackage toEntity(CigarettePackageDTO dto) {
        if (dto == null) return null; // Tránh null

        return CigarettePackage.builder()
                .cigaretteName(dto.getCigaretteName())      // Gán tên
                .price(dto.getPrice())                      // Gán giá
                .sticksPerPack(dto.getSticksPerPack())      // Gán số điếu
                .nicotineMg(dto.getNicotineMg())            // ✅ Gán nicotine (nếu bạn cho phép cập nhật)
                .build();
    }
}