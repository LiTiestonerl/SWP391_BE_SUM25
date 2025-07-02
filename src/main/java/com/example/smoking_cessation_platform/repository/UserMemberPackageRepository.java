package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.UserMemberPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UserMemberPackageRepository extends JpaRepository<UserMemberPackage, Integer>, JpaSpecificationExecutor<UserMemberPackage> {

}