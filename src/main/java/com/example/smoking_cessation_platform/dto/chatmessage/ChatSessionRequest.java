package com.example.smoking_cessation_platform.dto.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionRequest {
    private Long userId;
    private Long coachId;
}
