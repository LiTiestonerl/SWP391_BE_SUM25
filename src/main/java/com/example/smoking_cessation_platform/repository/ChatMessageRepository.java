package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer>, JpaSpecificationExecutor<ChatMessage> {

}