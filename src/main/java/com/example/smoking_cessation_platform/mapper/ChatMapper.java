package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageDTO;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionResponse;
import com.example.smoking_cessation_platform.entity.ChatMessage;
import com.example.smoking_cessation_platform.entity.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    // ✅ Map ChatSession entity -> ChatSessionResponse DTO
    public ChatSessionResponse toSessionResponse(ChatSession s) {
        if (s == null) return null;
        return ChatSessionResponse.builder()
                .sessionId(s.getSessionId())
                .startTime(s.getStartTime() != null ? s.getStartTime().toString() : null)
                .endTime(s.getEndTime() != null ? s.getEndTime().toString() : null)
                .status(s.getStatus())
                .userId(s.getUser().getUserId())
                .userName(s.getUser().getFullName())
                .coachId(s.getCoach().getUserId())
                .coachName(s.getCoach().getFullName())
                .build();
    }

    // ✅ Map ChatMessage entity -> ChatMessageDTO
    public ChatMessageDTO toMessageDTO(ChatMessage m) {
        if (m == null) return null;
        return ChatMessageDTO.builder()
                .messageId(m.getMessageId())
                .sessionId(m.getChatSession().getSessionId())
                .senderId(m.getSender().getUserId())
                .message(m.getMessage())
                .timestamp(m.getTimestamp() != null ? m.getTimestamp().toString() : null)
                .status(m.getStatus())
                .build();
    }
}