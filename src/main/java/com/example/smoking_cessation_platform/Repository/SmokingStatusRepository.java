package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.SmokingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmokingStatusRepository extends JpaRepository<SmokingStatus, Integer>, JpaSpecificationExecutor<SmokingStatus> {

}