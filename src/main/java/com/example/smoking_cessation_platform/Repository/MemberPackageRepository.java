package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.MemberPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberPackageRepository extends JpaRepository<MemberPackage, Integer>, JpaSpecificationExecutor<MemberPackage> {

}