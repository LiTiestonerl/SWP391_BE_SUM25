package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CigarettePackageRepository extends JpaRepository<CigarettePackage, Long>, JpaSpecificationExecutor<CigarettePackage> {

}