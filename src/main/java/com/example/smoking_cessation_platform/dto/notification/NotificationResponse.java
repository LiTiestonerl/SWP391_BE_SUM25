package com.example.smoking_cessation_platform.dto.notification;

import com.example.smoking_cessation_platform.Enum.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Integer notificationId;
    private String content;
    private String notificationType;
    private LocalDateTime sendDate;
    private NotificationStatus status;
    private Long userId;
    private Integer quitPlanId;
    private Integer achievementBadgeId;
}