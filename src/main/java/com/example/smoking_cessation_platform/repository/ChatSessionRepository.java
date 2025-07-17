package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer>, JpaSpecificationExecutor<ChatSession> {
    List<ChatSession> findByUser_UserIdOrCoach_UserId(Long userId, Long coachId);

}