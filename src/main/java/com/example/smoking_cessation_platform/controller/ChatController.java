package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageDTO;
import com.example.smoking_cessation_platform.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Nhận tin nhắn từ client tại /app/chat.send và broadcast tới /topic/session.{sessionId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessageDTO);
        String destination = "/topic/session." + savedMessage.getSessionId();
        messagingTemplate.convertAndSend(destination, savedMessage);
    }
}
