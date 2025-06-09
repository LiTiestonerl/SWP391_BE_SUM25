package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer>, JpaSpecificationExecutor<UserBadge> {

}