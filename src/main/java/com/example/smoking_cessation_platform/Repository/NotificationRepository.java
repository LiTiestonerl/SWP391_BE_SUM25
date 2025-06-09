package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {

}