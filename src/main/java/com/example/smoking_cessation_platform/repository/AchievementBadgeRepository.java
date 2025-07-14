package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.AchievementBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AchievementBadgeRepository extends JpaRepository<AchievementBadge, Integer>, JpaSpecificationExecutor<AchievementBadge> {
    Optional<AchievementBadge> findByBadgeIdAndDeletedFalse(Integer badgeId);

    Optional<AchievementBadge> findByBadgeNameAndDeletedFalse(String badgeName);

    List<AchievementBadge> findAllByDeletedFalse();

    Optional<AchievementBadge> findByBadgeType(String badgeType);
}