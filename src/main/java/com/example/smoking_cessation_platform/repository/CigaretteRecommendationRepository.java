package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CigaretteRecommendationRepository extends JpaRepository<CigaretteRecommendation, Integer>, JpaSpecificationExecutor<CigaretteRecommendation> {

}