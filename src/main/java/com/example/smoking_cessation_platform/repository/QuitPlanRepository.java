package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer>, JpaSpecificationExecutor<QuitPlan> {

    Optional<QuitPlan> findFirstByUser_UserIdAndStatus(Long userId, QuitPlanStatus quitPlanStatus);

    List<QuitPlan> findByCoachIsNull();

    List<QuitPlan> findByUser_UserId(Long userId);
}