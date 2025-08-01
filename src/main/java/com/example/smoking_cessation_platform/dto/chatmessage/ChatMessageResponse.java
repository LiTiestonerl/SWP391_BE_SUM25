package com.example.smoking_cessation_platform.dto.chatmessage;

import com.example.smoking_cessation_platform.Enum.MessageStatus;
import com.example.smoking_cessation_platform.Enum.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    private Integer messageId;
    private String message;
    private LocalDateTime timestamp;
    private MessageStatus status;
    private Long senderId;
}
