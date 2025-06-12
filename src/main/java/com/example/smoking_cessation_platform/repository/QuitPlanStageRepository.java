package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.QuitPlanStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuitPlanStageRepository extends JpaRepository<QuitPlanStage, Integer>, JpaSpecificationExecutor<QuitPlanStage> {

}