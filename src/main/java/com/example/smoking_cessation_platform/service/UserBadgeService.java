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
        UserBadge userBadge = userBadgeRepository.findById(userBadgeId)
                .orElseThrow(()->new ResourceNotFoundException("UserBadge",userBadgeId));
        return List.of(userBadgeMapper.toResponse(userBadge));
    }

    /**
     * 3. Chia sẻ huy hiệu
     */
    public List<UserBadgeResponse> shareUserBadge(Integer id) {
        UserBadge badge = userBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserBadge", id));
        badge.setShared(true);
        userBadgeRepository.save(badge);
        return List.of(userBadgeMapper.toResponse(badge));
    }

    /**
     * 4. Hủy chia sẻ huy hiệu
     */
    public List<UserBadgeResponse> unshareUserBadge(Integer id) {
        UserBadge badge = userBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserBadge", id));
        badge.setShared(false);
        userBadgeRepository.save(badge);
        return List.of(userBadgeMapper.toResponse(badge));
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


}
