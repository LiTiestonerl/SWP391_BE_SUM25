package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.notification.NotificationResponse;
import com.example.smoking_cessation_platform.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationResponse toResponse(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setNotificationId(n.getNotificationId());
        res.setContent(n.getContent());
        res.setNotificationType(n.getNotificationType());
        res.setSendDate(n.getSendDate());
        res.setStatus(n.getStatus());
        res.setUserId(n.getUser().getUserId());
        res.setQuitPlanId(n.getQuitPlan() != null ? n.getQuitPlan().getPlanId() : null);
        res.setAchievementBadgeId(n.getAchievementBadge() != null ? n.getAchievementBadge().getBadgeId() : null);
        return res;
    }
}
