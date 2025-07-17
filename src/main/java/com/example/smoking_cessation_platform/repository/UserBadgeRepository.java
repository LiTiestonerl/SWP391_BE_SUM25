package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer>, JpaSpecificationExecutor<UserBadge> {
    // ✅ Lấy tất cả huy hiệu của user
    List<UserBadge> findByUser_UserId(Long userId);

    // ✅ Lấy tất cả huy hiệu đã chia sẻ của user
    List<UserBadge> findByUser_UserIdAndSharedTrue(Long userId);

    boolean existsByUser_UserIdAndBadge_BadgeId(Long userId, Integer badgeId);

    @Query("SELECT b.badgeId, b.badgeName, COUNT(ub.userBadgeId) AS badgeCount " +
            "FROM UserBadge ub JOIN ub.badge b " +
            "GROUP BY b.badgeId, b.badgeName " +
            "ORDER BY badgeCount DESC")
    List<Object[]> findTopBadges();
}