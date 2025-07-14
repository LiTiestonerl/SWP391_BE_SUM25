package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    // Hàm chuyển đổi từ entity CigaretteRecommendation sang DTO RecommendationResponse
    public RecommendationResponse toDTO(CigaretteRecommendation rec) {
        // Nếu entity là null thì trả về null để tránh lỗi NullPointerException
        if (rec == null) return null;

        // Dùng builder để tạo đối tượng DTO từ entity
        return RecommendationResponse.builder()
                .recId(rec.getRecId()) // Gán ID của bản ghi recommendation

                // Gán ID của gói thuốc gốc (người dùng đang sử dụng)
                .fromPackageId(rec.getFromPackage().getCigaretteId())

                // Gán ID của gói thuốc được đề xuất thay thế
                .toPackageId(rec.getToPackage().getCigaretteId())

                // Gán ghi chú mô tả đề xuất (nếu có)
                .notes(rec.getNotes())

                // ✅ Gán thêm thông tin chi tiết về gói thuốc được đề xuất (toPackage)
                .toPackageDetail(CigarettePackageDTO.builder()
                        .cigaretteId(rec.getToPackage().getCigaretteId())
                        .cigaretteName(rec.getToPackage().getCigaretteName())
                        .price(rec.getToPackage().getPrice())
                        .sticksPerPack(rec.getToPackage().getSticksPerPack())
                        .build()
                )

                // Hoàn tất build DTO
                .build();
    }
}
