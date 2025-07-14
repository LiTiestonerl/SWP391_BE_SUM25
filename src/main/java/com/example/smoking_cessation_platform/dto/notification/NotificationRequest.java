package com.example.smoking_cessation_platform.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String content;
    private String notificationType;
    private LocalDateTime sendDate;
    private String status;
    private Long userId;
    private Integer quitPlanId;
    private Integer achievementBadgeId;
}