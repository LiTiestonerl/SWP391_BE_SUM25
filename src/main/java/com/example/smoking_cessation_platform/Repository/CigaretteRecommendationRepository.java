package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.CigaretteRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CigaretteRecommendationRepository extends JpaRepository<CigaretteRecommendation, Integer>, JpaSpecificationExecutor<CigaretteRecommendation> {

}