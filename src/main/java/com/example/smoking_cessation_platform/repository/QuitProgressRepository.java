package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.QuitProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuitProgressRepository extends JpaRepository<QuitProgress, Integer>, JpaSpecificationExecutor<QuitProgress> {

}