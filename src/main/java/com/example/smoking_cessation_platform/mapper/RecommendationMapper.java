package com.example.smoking_cessation_platform.mapper;

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
                .recId(rec.getRecId())   // Gán ID của đề xuất (rec_id)

                // Lấy ID của gói thuốc ban đầu (fromPackage là 1 đối tượng CigarettePackage)
                .fromPackageId(rec.getFromPackage().getCigaretteId())

                // Lấy ID của gói thuốc được đề xuất thay thế
                .toPackageId(rec.getToPackage().getCigaretteId())

                // Ghi chú thêm về đề xuất (notes là chuỗi mô tả)
                .notes(rec.getNotes())

                // Hoàn tất build DTO
                .build();
    }
}
