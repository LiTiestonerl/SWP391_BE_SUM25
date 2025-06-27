package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer>, JpaSpecificationExecutor<QuitPlan> {

}