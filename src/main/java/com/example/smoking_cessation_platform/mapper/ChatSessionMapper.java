package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionResponse;
import com.example.smoking_cessation_platform.entity.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionMapper {
    public ChatSessionResponse toResponse(ChatSession session){
        if(session == null){
            return null;
        }

        return new ChatSessionResponse(
                session.getSessionId() != null ? session.getSessionId().longValue():null,
                session.getUser() != null ? session.getUser().getUserId() : null,
                session.getCoach() != null ? session.getCoach().getUserId() : null
        );
    }
}
