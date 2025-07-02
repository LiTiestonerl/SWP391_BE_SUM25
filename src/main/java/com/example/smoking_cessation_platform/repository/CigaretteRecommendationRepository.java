package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CigaretteRecommendationRepository extends JpaRepository<CigaretteRecommendation, Integer>, JpaSpecificationExecutor<CigaretteRecommendation> {
    List<CigaretteRecommendation> findByFromPackage(CigarettePackage fromPackage);
}