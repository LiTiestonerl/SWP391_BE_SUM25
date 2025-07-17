package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.MemberPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemberPackageRepository extends JpaRepository<MemberPackage, Integer> {
    Optional<MemberPackage> findById (Integer memberPackageId);
    @Query("SELECT COUNT(ump.id) FROM UserMemberPackage ump")
    long countTotalRegistered();

    @Query("SELECT mp.memberPackageId, mp.packageName, COUNT(ump.userMemberPackageId) AS registerCount " +
            "FROM MemberPackage mp LEFT JOIN mp.userMemberPackages ump " +
            "GROUP BY mp.memberPackageId, mp.packageName " +
            "ORDER BY registerCount DESC")
    List<Object[]> findTopPackages();
}
