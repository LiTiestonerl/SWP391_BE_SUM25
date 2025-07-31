package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.Enum.NotificationStatus;
import com.example.smoking_cessation_platform.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByUser_UserId(Long userId);
    Optional<Notification> findByNotificationIdAndUser_UserId(Integer notificationId, Long userId);

    List<Notification> findByUser_UserIdAndStatusNot(Long userId, NotificationStatus notificationStatus);
}