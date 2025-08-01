package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.CigaretteRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CigaretteRecommendationRepository extends JpaRepository<CigaretteRecommendation, Integer>, JpaSpecificationExecutor<CigaretteRecommendation> {

    // Tìm thuốc lá nhẹ hơn dựa trên nicoteneStrength (Enum)
    @Query("SELECT cr FROM CigaretteRecommendation cr " +
            "WHERE cr.fromPackage.cigaretteId = :cigaretteId " +
            "AND cr.toPackage.nicoteneStrength < cr.fromPackage.nicoteneStrength " +
            "ORDER BY cr.toPackage.nicoteneStrength ASC")
    List<CigaretteRecommendation> findLighterNicotineRecommendations(@Param("cigaretteId") Long cigaretteId);

    // Tìm cùng flavor
    @Query("SELECT cr FROM CigaretteRecommendation cr " +
            "WHERE cr.fromPackage.cigaretteId = :cigaretteId " +
            "AND cr.toPackage.flavor = cr.fromPackage.flavor " +
            "AND cr.toPackage.cigaretteId != cr.fromPackage.cigaretteId")
    List<CigaretteRecommendation> findSameFlavorRecommendations(@Param("cigarette_id") Long cigaretteId);

    // Tìm cùng brand nhưng nhẹ hơn
    @Query("SELECT cr FROM CigaretteRecommendation cr " +
            "WHERE cr.fromPackage.cigaretteId = :cigaretteId " +
            "AND cr.toPackage.brand = cr.fromPackage.brand " +
            "AND cr.toPackage.nicoteneStrength < cr.fromPackage.nicoteneStrength")
    List<CigaretteRecommendation> findSameBrandLighterRecommendations(@Param("cigarette_id") Long cigaretteId);

    // Tìm thuốc lá cùng brand và flavor nhưng nhẹ hơn
    @Query("SELECT cr FROM CigaretteRecommendation cr " +
            "WHERE cr.fromPackage.cigaretteId = :cigaretteId " +
            "AND cr.toPackage.brand = cr.fromPackage.brand " +
            "AND cr.toPackage.flavor = cr.fromPackage.flavor " +
            "AND cr.toPackage.nicoteneStrength < cr.fromPackage.nicoteneStrength")
    List<CigaretteRecommendation> findSameBrandFlavorLighterRecommendations(@Param("cigarette_id") Long cigaretteId);

    // Tìm theo trạng thái hút thuốc (cập nhật cho Enum)
    public abstract List<CigaretteRecommendation> findBySmokingStatus_StatusId(Integer statusId);

    // Xóa các đề xuất thuốc lá liên quan đến một loại thuốc (package)
    void deleteByToPackage_CigaretteId(Long cigaretteId);

    // Xóa các đề xuất thuốc lá liên quan đến một loại thuốc (package)
    void deleteByFromPackage_CigaretteId(Long cigaretteId);
}
