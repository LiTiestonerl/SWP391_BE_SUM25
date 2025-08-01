package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.Enum.MessageStatus;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageRequest;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageResponse;
import com.example.smoking_cessation_platform.entity.ChatMessage;
import com.example.smoking_cessation_platform.entity.ChatSession;
import com.example.smoking_cessation_platform.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class ChatMessageMapper {

    public ChatMessageResponse toResponse(ChatMessage entity) {
        if (entity == null) return null;

        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId(entity.getMessageId());
        response.setMessage(entity.getMessage());
        response.setTimestamp(entity.getTimestamp());
        response.setStatus(entity.getStatus());

        if (entity.getSender() != null) {
            response.setSenderId(entity.getSender().getUserId());
        }

        return response;
    }

    // Request → Entity
    public ChatMessage toEntity(ChatMessageRequest request, User sender, ChatSession session) {
        if (request == null || sender == null || session == null) return null;

        return ChatMessage.builder()
                .message(request.getMessage())
                .sender(sender)
                .chatSession(session)
                .status(MessageStatus.ACTIVE)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
