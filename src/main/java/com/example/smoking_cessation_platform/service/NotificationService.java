package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.notification.NotificationRequest;
import com.example.smoking_cessation_platform.dto.notification.NotificationResponse;
import com.example.smoking_cessation_platform.entity.AchievementBadge;
import com.example.smoking_cessation_platform.entity.Notification;
import com.example.smoking_cessation_platform.entity.QuitPlan;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.mapper.NotificationMapper;
import com.example.smoking_cessation_platform.repository.AchievementBadgeRepository;
import com.example.smoking_cessation_platform.repository.NotificationRepository;
import com.example.smoking_cessation_platform.repository.QuitPlanRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private AchievementBadgeRepository achievementBadgeRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * Tạo mới một thông báo từ request
     */
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = new Notification();

        // Gán nội dung cơ bản từ request
        notification.setContent(request.getContent());
        notification.setNotificationType(request.getNotificationType());
        notification.setSendDate(request.getSendDate());
        notification.setStatus(request.getStatus() != null ? request.getStatus() : "sent");

        // Lấy user từ DB và gán vào notification
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        notification.setUser(user);

        // Nếu có QuitPlanId thì tìm kế hoạch và gán
        if (request.getQuitPlanId() != null) {
            QuitPlan plan = quitPlanRepository.findById(request.getQuitPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", request.getQuitPlanId()));
            notification.setQuitPlan(plan);
        }

        // Nếu có AchievementBadgeId thì tìm badge và gán
        if (request.getAchievementBadgeId() != null) {
            AchievementBadge badge = achievementBadgeRepository.findById(request.getAchievementBadgeId())
                    .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", request.getAchievementBadgeId()));
            notification.setAchievementBadge(badge);
        }

        // Lưu vào DB và ánh xạ sang DTO trả về
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    /**
     * Lấy danh sách thông báo theo người dùng
     */
    public List<NotificationResponse> getByUser(Long userId) {
        return notificationRepository.findByUser_UserId(userId)
                .stream()
                .map(notificationMapper::toResponse) // ánh xạ từng entity sang DTO
                .collect(Collectors.toList());
    }
}