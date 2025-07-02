package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.stereotype.Component;

@Component
public class CigarettePackageMapper {

    // Hàm chuyển đổi từ entity -> DTO
    public CigarettePackageDTO toDTO(CigarettePackage pkg) {
        // Nếu đối tượng truyền vào là null, trả về null để tránh lỗi NullPointerException
        if (pkg == null) return null;

        // Dùng builder để tạo đối tượng DTO từ entity
        return CigarettePackageDTO.builder()
                .cigaretteId(pkg.getCigaretteId())       // Lấy ID của gói thuốc
                .cigaretteName(pkg.getCigaretteName())   // Lấy tên gói thuốc
                .price(pkg.getPrice())                   // Lấy giá
                .sticksPerPack(pkg.getSticksPerPack())   // Lấy số điếu trong mỗi gói
                .build();                                // Kết thúc build DTO
    }

    // Hàm chuyển đổi từ DTO -> entity (dùng khi tạo hoặc cập nhật dữ liệu)
    public CigarettePackage toEntity(CigarettePackageDTO dto) {
        // Tránh lỗi nếu DTO null
        if (dto == null) return null;

        return CigarettePackage.builder()
                .cigaretteName(dto.getCigaretteName())   // Gán tên từ DTO
                .price(dto.getPrice())                   // Gán giá từ DTO
                .sticksPerPack(dto.getSticksPerPack())   // Gán số điếu mỗi gói từ DTO
                .build();                                // Kết thúc build entity
    }
}