package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.quitplan.QuitProgressRequest;
import com.example.smoking_cessation_platform.dto.quitplan.QuitProgressResponse;
import com.example.smoking_cessation_platform.entity.*;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.repository.QuitPlanStageRepository;
import com.example.smoking_cessation_platform.repository.QuitProgressRepository;
import com.example.smoking_cessation_platform.repository.SmokingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class QuitProgressService {

    @Autowired
    private QuitPlanStageRepository quitPlanStageRepository;

    @Autowired
    private QuitProgressRepository quitProgressRepository;

    @Autowired
    private SmokingStatusRepository smokingStatusRepository;

    public QuitProgressResponse updateProgress(QuitProgressRequest request) {

        // [1] Lấy Stage và QuitPlan
        QuitPlanStage stage = quitPlanStageRepository.findById(request.getStageId())
                .orElseThrow(() -> new ResourceNotFoundException("Stage", request.getStageId()));
        QuitPlan quitPlan = stage.getQuitPlan();

        // [2] Lấy SmokingStatus mới nhất của người dùng
        Long userId = quitPlan.getUser().getUserId();
        SmokingStatus smokingStatus = smokingStatusRepository.findTopByUser_UserIdOrderByRecordDateDesc(userId)
                .orElse(null);

        // [3] Lấy thông tin gói thuốc
        CigarettePackage usedPackage = Optional.ofNullable(quitPlan.getRecommendedPackage())
                .orElseGet(() -> smokingStatus != null ? smokingStatus.getCigarettePackage() : null);

        BigDecimal pricePerPack = (usedPackage != null && usedPackage.getPrice() != null)
                ? usedPackage.getPrice()
                : BigDecimal.ZERO;

        int sticksPerPack = (usedPackage != null && usedPackage.getSticksPerPack() != null)
                ? usedPackage.getSticksPerPack()
                : 20; // fallback

        BigDecimal pricePerCigarette = pricePerPack.divide(BigDecimal.valueOf(sticksPerPack), 2, RoundingMode.HALF_UP);

        // [4] Tính số điếu dự kiến và thực tế
        int expectedPerDay = (stage.getTargetCigarettesPerDay() != null)
                ? stage.getTargetCigarettesPerDay()
                : (smokingStatus != null ? smokingStatus.getCigarettesPerDay() : 0);

        int actualSmoked = (request.getCigarettesSmoked() != null) ? request.getCigarettesSmoked() : 0;

        // [5] Tính chi phí
        BigDecimal moneySpent = pricePerCigarette.multiply(BigDecimal.valueOf(actualSmoked));
        BigDecimal expectedSpent = pricePerCigarette.multiply(BigDecimal.valueOf(expectedPerDay));
        BigDecimal moneySaved = expectedSpent.subtract(moneySpent).max(BigDecimal.ZERO);
        int smokingFreeDays = (actualSmoked == 0) ? 1 : 0;

        // [6] Tìm hoặc tạo mới QuitProgress
        QuitProgress progress = quitProgressRepository
                .findByDateAndQuitPlanStage_StageId(request.getDate(), Long.valueOf(request.getStageId()))
                .orElse(new QuitProgress());

        progress.setDate(request.getDate());
        progress.setCigarettesSmoked(actualSmoked);
        progress.setMoneySpent(moneySpent);
        progress.setMoneySaved(moneySaved);
        progress.setSmokingFreeDays(smokingFreeDays);
        progress.setHealthStatus(request.getHealthStatus());
        progress.setQuitPlanStage(stage);

        quitProgressRepository.save(progress);

        // [7] Map response
        return QuitProgressResponse.builder()
                .progressId(progress.getProgressId())
                .date(progress.getDate())
                .cigarettesSmoked(progress.getCigarettesSmoked())
                .moneySpent(progress.getMoneySpent())
                .moneySaved(progress.getMoneySaved())
                .smokingFreeDays(progress.getSmokingFreeDays())
                .healthStatus(progress.getHealthStatus())
                .stageId(stage.getStageId())
                .stageName(stage.getStageName())
                .build();
    }
}
