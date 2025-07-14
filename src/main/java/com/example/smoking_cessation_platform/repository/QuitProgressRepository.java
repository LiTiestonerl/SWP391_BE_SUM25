package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.QuitProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface QuitProgressRepository extends JpaRepository<QuitProgress, Integer>, JpaSpecificationExecutor<QuitProgress> {

    Set<QuitProgress> findByQuitPlanStage_StageId(Integer stageId);
}