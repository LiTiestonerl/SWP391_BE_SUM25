package com.example.smoking_cessation_platform.dto.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionResponse {
    private Integer sessionId;
    private String startTime;
    private String endTime;
    private String status;

    private Long userId;
    private String userName;

    private Long coachId;
    private String coachName;
}
