package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.Enum.MessageStatus;
import com.example.smoking_cessation_platform.Enum.SessionStatus;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageRequest;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatMessageResponse;
import com.example.smoking_cessation_platform.dto.chatmessage.ChatSessionResponse;
import com.example.smoking_cessation_platform.entity.ChatMessage;
import com.example.smoking_cessation_platform.entity.ChatSession;
import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.exception.ForbiddenActionException;
import com.example.smoking_cessation_platform.exception.NotFoundException;
import com.example.smoking_cessation_platform.exception.UnauthorizedException;
import com.example.smoking_cessation_platform.mapper.ChatMessageMapper;
import com.example.smoking_cessation_platform.mapper.ChatSessionMapper;
import com.example.smoking_cessation_platform.repository.ChatMessageRepository;
import com.example.smoking_cessation_platform.repository.ChatSessionRepository;
import com.example.smoking_cessation_platform.repository.UserMemberPackageRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

    private static final Long FREE_PACKAGE_ID = 10L;
    private static final String ROLE_COACH_NAME = "ROLE_COACH";

    public ChatSessionResponse createSession(Long userId, Long coachId) {

        if (!canUserChat(userId)) {
            throw new ForbiddenActionException("Bạn không thể thực hiện chức năng này, hãy nâng cấp gói membership");
        }

        Optional<ChatSession> exitingSession = chatSessionRepository.findByUser_UserIdAndCoach_UserId(userId,coachId);
        if(exitingSession.isPresent()){
            return chatSessionMapper.toResponse(exitingSession.get());
        }

        ChatSession session =  new ChatSession();
        session.setUser(userRepository.findByUserId(userId).
                orElseThrow(()-> new NotFoundException("User không tồn tại")));
        session.setCoach(userRepository.findByUserId(coachId)
                .orElseThrow(()-> new NotFoundException("Coach không tồn tại")));
        session.setCreatedAt(LocalDateTime.now());

        ChatSession saveSession = chatSessionRepository.save(session);

        return chatSessionMapper.toResponse(saveSession);
    }


    public boolean canUserChat(Long userId){

        Optional<User> optionalUser =  userRepository.findById(userId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            Role role = user.getRole();
            if(role!=null && ROLE_COACH_NAME.equals(role.getRoleName())){
                return true;
            }
        }
        return userMemberPackageRepository
                .findFirstByUser_UserIdAndStatusAndMemberPackage_MemberPackageIdNotOrderByStartDateDesc(
                        userId, "active", FREE_PACKAGE_ID
                ).isPresent();
    }


    public ChatMessageResponse sendMessage(Integer sessionId, Long userId, ChatMessageRequest request) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new NotFoundException("chat session không tồn tại"));
        if (!session.getUser().getUserId().equals(userId) && !session.getCoach().getUserId().equals(userId)){
            throw new UnauthorizedException("Bạn không thể gủi tịn nhắn");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người gửi không tồn tại"));

        ChatMessage message = ChatMessage.builder()
                .chatSession(session)
                .sender(sender)
                .message(request.getMessage())
                .build();

        chatMessageRepository.save(message);
        return chatMessageMapper.toResponse(message);

    }

    public List<ChatMessageResponse> getMessage(Integer sessionId, Long userId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new NotFoundException("Chat session không tồn tại"));

        if (!session.getUser().getUserId().equals(userId) && !session.getCoach().getUserId().equals(userId)){
            throw new UnauthorizedException("Bạn không thể gửi tin nhắn");
        }

        if ((session.getUser().getUserId().equals(userId) && session.isDeletedByUser()) ||
                (session.getCoach().getUserId().equals(userId) && session.isDeletedByCoach())) {
            throw new NotFoundException("Phiên chat này đã bị xoá");
        }


        List<ChatMessage> messages = chatMessageRepository
                .findByChatSessionAndStatusOrderByTimestampAsc(session, MessageStatus.ACTIVE);

        return messages.stream()
                .filter(msg -> {
                    // Nếu là user thì chỉ lấy tin chưa bị xóa bởi user
                    if (session.getUser().getUserId().equals(userId)) {
                        return !msg.isDeletedByUser();
                    }
                    // Nếu là coach thì chỉ lấy tin chưa bị xóa bởi coach
                    else if (session.getCoach().getUserId().equals(userId)) {
                        return !msg.isDeletedByCoach();
                    }
                    return false;
                })
                .map(chatMessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ChatSessionResponse> getSessionByUserId(Long userId) {
        List<ChatSession> userSession = chatSessionRepository.findByUser_UserIdAndDeletedByUserFalse(userId);

        List<ChatSession> coachSession = chatSessionRepository.findByCoach_UserIdAndDeletedByCoachFalse(userId);

        // Gộp và loại trùng nếu có
        Set<ChatSession> allSessions = new HashSet<>();
        allSessions.addAll(userSession);
        allSessions.addAll(coachSession);

        return allSessions.stream()
                .map(chatSessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void softDeleteSession(Integer sessionId, Long userId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new NotFoundException("Chat session không tồn tại "));

        if(session.getUser().getUserId().equals(userId)){
            session.setDeletedByUser(true);
        } else if (session.getCoach().getUserId().equals(userId)) {
            session.setDeletedByCoach(true);
        } else {
            throw new UnauthorizedException("Bạn không có quyền xó");
        }

        if (session.isDeletedByUser() && session.isDeletedByCoach()) {
            session.setStatus(SessionStatus.DELETED);
        }
        chatSessionRepository.save(session);
    }

    public void deleteMessage(Integer sessionId, Integer messageId, Long userId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new NotFoundException("chat session không tòn tại"));

        if (!session.getUser().getUserId().equals(userId) &&
                !session.getCoach().getUserId().equals(userId)) {
            throw new UnauthorizedException("Bạn không có quyền với session này");
        }

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(()-> new NotFoundException("Tin nhắn không tồn tại"));

        if (!message.getSender().getUserId().equals(userId)) {
            throw new ForbiddenActionException("Chỉ người gửi mới được xóa");
        }

        // Đánh dấu xoá theo đúng vai trò của sender
        if (session.getUser().getUserId().equals(message.getSender().getUserId())) {
            message.setDeletedByUser(true);
        } else if (session.getCoach().getUserId().equals(message.getSender().getUserId())) {
            message.setDeletedByCoach(true);
        }


        if(message.isDeletedByUser() && message.isDeletedByCoach()){
            message.setStatus(MessageStatus.DELETED);
        };
        chatMessageRepository.save(message);
    }

    public List<ChatSessionResponse> getSessionByCoachId(Long userId) {
        List<ChatSession> coachSessions = chatSessionRepository
                .findByCoach_UserIdAndDeletedByCoachFalse(userId);

        List<ChatSession> userSessions = chatSessionRepository
                .findByUser_UserIdAndDeletedByUserFalse(userId);

        Set<ChatSession> allSessions = new HashSet<>();
        allSessions.addAll(coachSessions);
        allSessions.addAll(userSessions);

        return allSessions.stream()
                .map(chatSessionMapper::toResponse)
                .collect(Collectors.toList());
    }
}

