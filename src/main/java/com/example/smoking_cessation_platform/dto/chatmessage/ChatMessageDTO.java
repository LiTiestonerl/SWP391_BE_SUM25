package com.example.smoking_cessation_platform.dto.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Integer sessionId;
    private Integer senderId;
    private String message;
    private String timestamp;
}
