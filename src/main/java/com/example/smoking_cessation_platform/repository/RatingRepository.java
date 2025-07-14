package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer>, JpaSpecificationExecutor<Rating> {
    List<Rating> findByCoachUserId(Long coachId);
    List<Rating> findByMemberUserId(Long memberId);
    List<Rating> findByQuitPlanPlanId(Integer planId);

    boolean existsByMemberUserIdAndQuitPlanPlanId(Long memberId, Integer planId);;
}