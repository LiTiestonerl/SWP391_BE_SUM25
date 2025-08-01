package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer>, JpaSpecificationExecutor<ChatSession> {
    Optional<ChatSession> findByUser_UserIdAndCoach_UserId(Long userId, Long coachId);

    List<ChatSession> findByUser_UserIdAndDeletedByUserFalse(Long userId);

    List<ChatSession> findByCoach_UserIdAndDeletedByCoachFalse(Long coachId);
}