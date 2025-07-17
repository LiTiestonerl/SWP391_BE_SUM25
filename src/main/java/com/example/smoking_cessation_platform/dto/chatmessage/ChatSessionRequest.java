package com.example.smoking_cessation_platform.dto.chatmessage;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.ArrayLen;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionRequest {
    private Long userId;   // id của user
    private Long coachId;  // id của coach
}
