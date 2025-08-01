package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.Enum.MessageStatus;
import com.example.smoking_cessation_platform.entity.ChatMessage;
import com.example.smoking_cessation_platform.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer>, JpaSpecificationExecutor<ChatMessage> {

    List<ChatMessage> findByChatSessionOrderByTimestampAsc(ChatSession chatSession);

    List<ChatMessage> findByChatSessionAndStatusOrderByTimestampAsc(ChatSession session, MessageStatus status);
}