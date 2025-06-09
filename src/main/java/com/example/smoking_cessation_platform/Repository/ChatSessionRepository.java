package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer>, JpaSpecificationExecutor<ChatSession> {

}