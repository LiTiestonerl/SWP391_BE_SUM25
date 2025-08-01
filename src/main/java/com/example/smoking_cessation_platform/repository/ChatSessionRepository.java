package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer>, JpaSpecificationExecutor<ChatSession> {

}