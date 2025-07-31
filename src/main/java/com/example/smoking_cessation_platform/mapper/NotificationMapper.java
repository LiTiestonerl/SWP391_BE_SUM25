package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.Enum.NotificationStatus;
import com.example.smoking_cessation_platform.dto.notification.NotificationResponse;
import com.example.smoking_cessation_platform.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationResponse toResponse(Notification n) {
        if (n == null) return null;

        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .content(n.getContent())
                .notificationType(n.getNotificationType())
                .sendDate(n.getSendDate())
                .status(n.getStatus()) // enum NotificationStatus
                .userId(n.getUser() != null ? n.getUser().getUserId() : null)
                .quitPlanId(n.getQuitPlan() != null ? n.getQuitPlan().getPlanId() : null)
                .achievementBadgeId(n.getAchievementBadge() != null ? n.getAchievementBadge().getBadgeId() : null)
                .build();
    }
}
