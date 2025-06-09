package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer>, JpaSpecificationExecutor<QuitPlan> {

}