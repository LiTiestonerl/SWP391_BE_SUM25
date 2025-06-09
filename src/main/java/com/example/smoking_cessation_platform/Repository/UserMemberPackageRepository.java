package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.UserMemberPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserMemberPackageRepository extends JpaRepository<UserMemberPackage, Integer>, JpaSpecificationExecutor<UserMemberPackage> {

}