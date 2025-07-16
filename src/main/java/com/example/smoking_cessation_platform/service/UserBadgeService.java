package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.userbadge.UserBadgeRequest;
import com.example.smoking_cessation_platform.dto.userbadge.UserBadgeResponse;
import com.example.smoking_cessation_platform.entity.AchievementBadge;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.UserBadge;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.mapper.UserBadgeMapper;
import com.example.smoking_cessation_platform.repository.AchievementBadgeRepository;
import com.example.smoking_cessation_platform.repository.UserBadgeRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SecurityRequirement(name = "api")
public class UserBadgeService {

    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AchievementBadgeRepository achievementBadgeRepository;
    @Autowired
    private UserBadgeMapper userBadgeMapper;

    /**
     * 1. Xem danh sách huy hiệu đã đạt được
     */
    public List<UserBadgeResponse> getBadgesByUserId(Long userId) {
        return userBadgeRepository.findByUser_UserId(userId)
                .stream()
                .map(userBadgeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 2. Xem chi tiết một userBadge
     */
    public List<UserBadgeResponse> getBadgeById(Integer userBadgeId) {
        UserBadge userBadge = getBadgeEntityOrThrow(userBadgeId);
        return List.of(userBadgeMapper.toResponse(userBadge));
    }

    /**
     * 3. Chia sẻ huy hiệu
     */
    @Transactional
    public List<UserBadgeResponse> shareUserBadge(Integer id, Long currentUserId) {
        if (!isOwnerOfBadge(id, currentUserId)) {
            throw new SecurityException("Bạn không có quyền chia sẻ huy hiệu này.");
        }

        UserBadge badge = getBadgeEntityOrThrow(id);
        badge.setShared(true);
        return List.of(userBadgeMapper.toResponse(userBadgeRepository.save(badge)));
    }

    /**
     * 4. Hủy chia sẻ huy hiệu
     */
    public List<UserBadgeResponse> unshareUserBadge(Integer id, Long currentUserId) {
        if (!isOwnerOfBadge(id, currentUserId)) {
            throw new SecurityException("Bạn không có quyền hủy chia sẻ huy hiệu này.");
        }

        UserBadge badge = getBadgeEntityOrThrow(id);
        badge.setShared(false);
        return List.of(userBadgeMapper.toResponse(userBadgeRepository.save(badge)));
    }


    /**
     * 5. Xem huy hiệu người khác đã chia sẻ
     */
    public List<UserBadgeResponse> getShareBadgesByUserId(Long userId) {
        return userBadgeRepository.findByUser_UserIdAndSharedTrue(userId)
                .stream()
                .map(userBadgeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public boolean isOwnerOfBadge(Integer badgeId, Long userId) {
        return userBadgeRepository.findById(badgeId)
                .map(badge -> badge.getUser().getUserId().equals(userId))
                .orElse(false);
    }

    /**
     * Lấy entity UserBadge hoặc ném lỗi nếu không tìm thấy.
     */
    private UserBadge getBadgeEntityOrThrow(Integer badgeId) {
        return userBadgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("UserBadge", badgeId));
    }

}
