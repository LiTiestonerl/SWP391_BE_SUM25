package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.MemberPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberPackageRepository extends JpaRepository<MemberPackage, Integer> {
    Optional<MemberPackage> findById (Integer memberPackageId);
}
