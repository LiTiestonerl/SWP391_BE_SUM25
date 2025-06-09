package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RatingRepository extends JpaRepository<Rating, Integer>, JpaSpecificationExecutor<Rating> {

}