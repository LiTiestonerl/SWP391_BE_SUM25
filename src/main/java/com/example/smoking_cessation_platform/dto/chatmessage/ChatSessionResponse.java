package com.example.smoking_cessation_platform.dto.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionResponse {
    private Long sessionId;
    private Long userId;
    private Long coachId;
}
