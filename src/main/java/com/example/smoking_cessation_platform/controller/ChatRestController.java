package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageDTO;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionRequest;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionResponse;
import com.example.smoking_cessation_platform.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("hasAnyRole('USER','COACH')")
public class ChatRestController {
    @Autowired
    private ChatService chatService;

    /**
     * Lấy danh sách session theo userId (user hoặc coach)
     * Example: GET /api/chat/sessions?userId=1
     */
    @Operation(summary = "Lấy danh sách session theo userId (user hoặc coach)")
    @GetMapping("/sessions")
    public List<ChatSessionResponse> getSessionsByUser(@RequestParam Long userId) {
        return chatService.getSessionsByUser(userId);
    }

    /**
     * Lấy chi tiết 1 session
     * Example: GET /api/chat/sessions/{id}
     */
    @Operation(summary = "Lấy chi tiết một session")
    @GetMapping("/sessions/{id}")
    public ChatSessionResponse getSessionDetail(@PathVariable Integer id) {
        return chatService.getSessionDetail(id);
    }

    /**
     * Lấy lịch sử tin nhắn theo session
     * Example: GET /api/chat/sessions/{id}/messages
     */
    @Operation(summary = "Lấy lịch sử tin nhắn theo session")
    @GetMapping("/sessions/{id}/messages")
    public List<ChatMessageDTO> getMessagesBySession(@PathVariable Integer id) {
        return chatService.getMessagesBySession(id);
    }

    /**
     * Tạo session mới giữa user và coach
     * Example: POST /api/chat/sessions
     * {
     *   "userId": 1,
     *   "coachId": 2
     * }
     */
    @Operation(summary = "Tạo session mới giữa user và coach")
    @PostMapping("/sessions")
    public ChatSessionResponse createSession(@RequestBody ChatSessionRequest request) {
        return chatService.createSession(request);
    }
}