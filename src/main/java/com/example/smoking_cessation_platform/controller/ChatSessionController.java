package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageRequest;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageResponse;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionResponse;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import com.example.smoking_cessation_platform.service.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-session")
@SecurityRequirement(name = "api")
public class ChatSessionController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/session")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatSessionResponse> createSession(@RequestParam Long coachId,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatSessionResponse response = chatService.createSession(userDetails.getUserId(), coachId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/session/{sessionId}/message")
    @PreAuthorize("hasAnyRole('USER', 'COACH')")
    public ResponseEntity<ChatMessageResponse> sendMessage(@PathVariable Integer sessionId,
                                                           @Valid @RequestBody ChatMessageRequest request,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatMessageResponse response = chatService.sendMessage(sessionId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}/message")
    @PreAuthorize("hasAnyRole('USER', 'COACH')")
    public ResponseEntity<List<ChatMessageResponse>> getMessage(@PathVariable Integer sessionId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatMessageResponse> messageResponses = chatService.getMessage(sessionId, userDetails.getUserId());
        return ResponseEntity.ok(messageResponses);
    }

    @GetMapping("/session")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChatSessionResponse>> getUserSession(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatSessionResponse> responses = chatService.getSessionByUserId(userDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/coach/session")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<List<ChatSessionResponse>> getCoachSession(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatSessionResponse> responses = chatService.getSessionByCoachId(userDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('USER', 'COACH')")
    public ResponseEntity<?> deleteChatSession(@PathVariable Integer sessionId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails){
        chatService.softDeleteSession(sessionId,userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/session/{sessionId}/message/{messageId}")
    @PreAuthorize("hasAnyRole('USER', 'COACH')")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer sessionId,
                                           @PathVariable Integer messageId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails){
        chatService.deleteMessage(sessionId,messageId,userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}

