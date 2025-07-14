package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageDTO;
import com.example.smoking_cessation_platform.entity.ChatMessage;
import com.example.smoking_cessation_platform.entity.ChatSession;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.repository.ChatMessageRepository;
import com.example.smoking_cessation_platform.repository.ChatSessionRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    public ChatMessageDTO saveMessage(ChatMessageDTO dto) {
        ChatSession session = chatSessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new RuntimeException("ChatSession not found"));

        User sender = userRepository.findById(Long.valueOf(dto.getSenderId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage message = ChatMessage.builder()
                .chatSession(session)
                .sender(sender)
                .message(dto.getMessage())
                .timestamp(LocalDateTime.now())
                .status("active")
                .build();

        chatMessageRepository.save(message);

        return ChatMessageDTO.builder()
                .sessionId(session.getSessionId())
                .senderId(Math.toIntExact(sender.getUserId()))  // ✅ Trả về Long thay vì ép int
                .message(message.getMessage())
                .timestamp(message.getTimestamp().toString())
                .build();
    }

}
