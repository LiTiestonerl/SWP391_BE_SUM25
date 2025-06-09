package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.QuitProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuitProgressRepository extends JpaRepository<QuitProgress, Integer>, JpaSpecificationExecutor<QuitProgress> {

}