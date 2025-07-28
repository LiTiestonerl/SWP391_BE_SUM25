package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageDTO;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionRequest;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionResponse;
import com.example.smoking_cessation_platform.entity.*;
import com.example.smoking_cessation_platform.mapper.ChatMapper;
import com.example.smoking_cessation_platform.repository.ChatMessageRepository;
import com.example.smoking_cessation_platform.repository.ChatSessionRepository;
import com.example.smoking_cessation_platform.repository.UserMemberPackageRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

    public ChatMessageDTO saveMessage(ChatMessageDTO dto) {
        // ✅ Validate cơ bản
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (dto.getSessionId() == null || dto.getSenderId() == null) {
            throw new IllegalArgumentException("SessionId and SenderId cannot be null");
        }

        // ✅ Lấy session
        ChatSession session = chatSessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new RuntimeException("ChatSession not found"));

        // ✅ Lấy sender
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Kiểm tra sender có thuộc session không
        if (!session.getUser().getUserId().equals(sender.getUserId()) &&
                !session.getCoach().getUserId().equals(sender.getUserId())) {
            throw new SecurityException("Sender not in this session");
        }

        // ✅ Tạo message entity với timestamp UTC
        LocalDateTime utcNow = LocalDateTime.now(ZoneId.of("UTC"));
        ChatMessage message = ChatMessage.builder()
                .chatSession(session)
                .sender(sender)
                .message(dto.getMessage())
                .timestamp(utcNow)
                .status("active")
                .build();

        // ✅ Lưu vào DB
        chatMessageRepository.save(message);

        // ✅ Logging
        log.info("Saved message {} from sender {} in session {}",
                message.getMessageId(), sender.getUserId(), session.getSessionId());

        // ✅ Trả về DTO đã lưu, bao gồm messageId & status
        return ChatMessageDTO.builder()
                .messageId(message.getMessageId())        // ✅ trả về messageId
                .sessionId(session.getSessionId())
                .senderId(sender.getUserId())
                .message(message.getMessage())
                .timestamp(message.getTimestamp().toString())
                .status(message.getStatus())              // ✅ trả về status
                .build();
    }

    public List<ChatSessionResponse> getSessionsByUser(Long userId) {
        return chatSessionRepository.findByUser_UserIdOrCoach_UserId(userId, userId)
                .stream()
                .map(chatMapper::toSessionResponse)
                .collect(Collectors.toList());
    }

    public ChatSessionResponse getSessionDetail(Integer id) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ChatSession not found"));
        return chatMapper.toSessionResponse(session);
    }

    public List<ChatMessageDTO> getMessagesBySession(Integer id) {
        return chatMessageRepository.findByChatSession_SessionId(id)
                .stream()
                .map(chatMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    public ChatSessionResponse createSession(ChatSessionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User coach = userRepository.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        // 2. Lấy gói active của user
        UserMemberPackage ump = userMemberPackageRepository
                .findFirstByUser_UserIdAndStatusOrderByStartDateDesc(request.getUserId(), "active")
                .orElseThrow(() -> new RuntimeException("User chưa có gói active"));

        MemberPackage memberPackage = ump.getMemberPackage();

        // 3. Kiểm tra gói có hỗ trợ chat không
        String packageName = memberPackage.getPackageName(); // ví dụ: "Free", "Health+", "Health Pro"
        if (!(packageName.equalsIgnoreCase("Health+") || packageName.equalsIgnoreCase("Health Pro"))) {
            throw new RuntimeException("Gói hiện tại (" + packageName + ") không hỗ trợ chat với coach.");
        }

        // 4. Kiểm tra coach có nằm trong danh sách hỗ trợ không
        boolean supportedCoach = memberPackage.getSupportedCoaches().stream()
                .anyMatch(c -> c.getUserId().equals(request.getCoachId()));

        if (!supportedCoach) {
            throw new RuntimeException("Coach này không được gói hiện tại hỗ trợ.");
        }

        // 5. Tạo session
        ChatSession session = ChatSession.builder()
                .user(user)
                .coach(coach)
                .startTime(LocalDateTime.now())
                .status("active")
                .build();

        chatSessionRepository.save(session);

        return chatMapper.toSessionResponse(session);
    }
}

