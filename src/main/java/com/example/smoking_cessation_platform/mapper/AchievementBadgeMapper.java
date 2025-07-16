package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeRequest;
import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeResponse;
import com.example.smoking_cessation_platform.entity.AchievementBadge;
import org.springframework.stereotype.Component;

@Component
public class AchievementBadgeMapper {

    /**
     * Chuyển dữ liệu từ request DTO (client gửi lên) thành entity (để lưu vào DB)
     *
     * @param request Dữ liệu tạo/cập nhật huy hiệu từ client
     * @return Đối tượng AchievementBadge entity
     */
    public AchievementBadge toEntity(AchievementBadgeRequest request) {
        if (request == null) return null;

        return AchievementBadge.builder()
                .badgeName(request.getBadgeName())        // Gán tên huy hiệu
                .description(request.getDescription())    // Gán mô tả
                .criteria(request.getCriteria())          // Gán tiêu chí đạt được
                .badgeType(request.getBadgeType())        // Gán loại huy hiệu (ví dụ: milestone, activity)
                .build();
    }

    /**
     * Chuyển dữ liệu từ entity (từ DB) thành response DTO (trả về client)
     *
     * @param badge Entity của AchievementBadge lấy từ DB
     * @return Đối tượng DTO chứa thông tin cần thiết
     */
    public AchievementBadgeResponse toResponse(AchievementBadge badge) {
        if (badge == null) return null;

        return AchievementBadgeResponse.builder()
                .badgeId(badge.getBadgeId())              // ID hệ thống của huy hiệu
                .badgeName(badge.getBadgeName())          // Tên huy hiệu
                .description(badge.getDescription())      // Mô tả
                .criteria(badge.getCriteria())            // Tiêu chí
                .badgeType(badge.getBadgeType())          // Loại huy hiệu
                .build();
    }
}
