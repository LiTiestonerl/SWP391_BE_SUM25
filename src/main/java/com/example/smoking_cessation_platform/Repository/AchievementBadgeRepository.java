package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.AchievementBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AchievementBadgeRepository extends JpaRepository<AchievementBadge, Integer>, JpaSpecificationExecutor<AchievementBadge> {

}