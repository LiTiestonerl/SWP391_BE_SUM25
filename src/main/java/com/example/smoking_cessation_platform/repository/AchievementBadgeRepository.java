package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.AchievementBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AchievementBadgeRepository extends JpaRepository<AchievementBadge, Integer>, JpaSpecificationExecutor<AchievementBadge> {

}