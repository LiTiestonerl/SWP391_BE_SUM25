package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.QuitPlanStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuitPlanStageRepository extends JpaRepository<QuitPlanStage, Integer>, JpaSpecificationExecutor<QuitPlanStage> {

}