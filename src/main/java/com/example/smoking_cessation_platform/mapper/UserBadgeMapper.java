package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.dto.userbadge.UserBadgeRequest;
import com.example.smoking_cessation_platform.dto.userbadge.UserBadgeResponse;
import com.example.smoking_cessation_platform.entity.AchievementBadge;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.UserBadge;
import com.example.smoking_cessation_platform.repository.AchievementBadgeRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserBadgeMapper {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AchievementBadgeRepository badgeRepository;

    public UserBadge toEntity(UserBadgeRequest request) {
        UserBadge userBadge = new UserBadge();
        userBadge.setDateAchieved(request.getDateAchieved());
        userBadge.setShared(Boolean.TRUE.equals(request.getShared()));

        User user = userRepository.findById((request.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        AchievementBadge badge = badgeRepository.findById(request.getBadgeId())
                .orElseThrow(() -> new IllegalArgumentException("Badge not found"));

        userBadge.setUser(user);
        userBadge.setBadge(badge);

        return userBadge;
    }

    public UserBadgeResponse toResponse(UserBadge entity) {
        UserBadgeResponse response = new UserBadgeResponse();
        response.setUserBadgeId(entity.getUserBadgeId());
        response.setDateAchieved(entity.getDateAchieved());
        response.setShared(entity.isShared());

        response.setUserId(entity.getUser().getUserId());
        response.setUsername(entity.getUser().getUserName());

        response.setBadgeId(entity.getBadge().getBadgeId());
        response.setBadgeName(entity.getBadge().getBadgeName());
        response.setBadgeDescription(entity.getBadge().getDescription());
        response.setBadgeCriteria(entity.getBadge().getCriteria());

        return response;
    }
}
