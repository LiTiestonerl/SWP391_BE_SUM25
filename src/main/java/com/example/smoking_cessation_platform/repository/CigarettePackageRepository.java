package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.CigarettePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CigarettePackageRepository extends JpaRepository<CigarettePackage, Long>, JpaSpecificationExecutor<CigarettePackage> {


    boolean existsByCigaretteName(String cigaretteName);
}
