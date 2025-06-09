package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer>, JpaSpecificationExecutor<ChatMessage> {

}