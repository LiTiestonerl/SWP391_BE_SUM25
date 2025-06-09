package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.CigarettePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CigarettePackageRepository extends JpaRepository<CigarettePackage, Long>, JpaSpecificationExecutor<CigarettePackage> {

}