package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeRequest;
import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeResponse;
import com.example.smoking_cessation_platform.entity.AchievementBadge;
import org.springframework.stereotype.Component;

@Component
public class AchievementBadgeMapper {

    // Chuyển đổi dữ liệu từ request DTO thành entity để lưu vào database
    public AchievementBadge toEntity(AchievementBadgeRequest request) {
        return AchievementBadge.builder()
                // Gán tên huy hiệu từ request
                .badgeName(request.getBadgeName())
                // Gán mô tả huy hiệu từ request
                .description(request.getDescription())
                // Gán tiêu chí đạt huy hiệu từ request
                .criteria(request.getCriteria())
                .build(); // Tạo đối tượng AchievementBadge hoàn chỉnh
    }

    // Chuyển đổi dữ liệu từ entity thành response DTO để trả về client
    public AchievementBadgeResponse toResponse(AchievementBadge badge) {
        return AchievementBadgeResponse.builder()
                // Lấy ID của huy hiệu
                .badgeId(badge.getBadgeId())
                // Lấy tên huy hiệu
                .badgeName(badge.getBadgeName())
                // Lấy mô tả huy hiệu
                .description(badge.getDescription())
                // Lấy tiêu chí đạt huy hiệu
                .criteria(badge.getCriteria())
                .build(); // Tạo đối tượng response hoàn chỉnh
    }
}
