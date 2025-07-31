package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.UserMemberPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserMemberPackageRepository extends JpaRepository<UserMemberPackage, Integer>, JpaSpecificationExecutor<UserMemberPackage> {

    boolean existsByUser_UserIdAndMemberPackage_SupportedCoaches_UserIdAndStatusIgnoreCase(Long userId, Long coachId, String active);
    @Modifying
    @Query("UPDATE UserMemberPackage ump SET ump.status = 'inactive' WHERE ump.user.userId = :userId AND ump.status = 'active'")
    void deactivateAllByUser(@Param("userId") Long userId);

    Optional<UserMemberPackage> findFirstByUser_UserIdAndStatusOrderByStartDateDesc(Long userId, String status);

    List<UserMemberPackage> findByUser_UserIdAndStatus(Long userId, String status);

    Optional<UserMemberPackage> findFirstByUser_UserIdAndStatusAndMemberPackage_MemberPackageIdNotOrderByStartDateDesc(
            Long userId, String status, Long excludedPackageId);

    Optional<UserMemberPackage> findFirstByUser_UserIdAndStatusAndMemberPackage_MemberPackageIdOrderByStartDateDesc(
            Long userId, String status, Long packageId);
}