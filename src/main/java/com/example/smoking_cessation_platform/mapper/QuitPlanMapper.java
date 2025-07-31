package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanRequest;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanResponse;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanStageResponse;
import com.example.smoking_cessation_platform.dto.quitplan.QuitProgressResponse;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.entity.QuitPlan;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuitPlanMapper {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  CigarettePackageRepository packageRepository;


    // 1. Convert từ request sang entity
    public QuitPlan toEntity(QuitPlanRequest request) {
        QuitPlan quitPlan = new QuitPlan();

        quitPlan.setTitle(request.getTitle());
        quitPlan.setStartDate(request.getStartDate());
        quitPlan.setExpectedEndDate(request.getExpectedEndDate());
        quitPlan.setReason(request.getReason());
        quitPlan.setStagesDescription(request.getStagesDescription());
        quitPlan.setCustomNotes(request.getCustomNotes());
        quitPlan.setCigarettesPerDay(request.getCigarettesPerDay());

        // Gán user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        quitPlan.setUser(user);

        // Gán coach nếu có
        if (request.getCoachId() != null) {
            User coach = userRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coach", request.getCoachId()));
            quitPlan.setCoach(coach);
        }

        // Gán recommendedPackage nếu có
        if (request.getRecommendedPackageId() != null) {
            CigarettePackage recommendedPackage = packageRepository.findById(request.getRecommendedPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recommended Package", request.getRecommendedPackageId()));
            quitPlan.setRecommendedPackage(recommendedPackage);
        }

        // ✅ Set mặc định là ACTIVE khi tạo mới
        quitPlan.setStatus(QuitPlanStatus.IN_PROGRESS);

        return quitPlan;
    }

    // 2. Convert từ entity sang response
    public QuitPlanResponse toResponse(QuitPlan plan) {
        QuitPlanResponse response = new QuitPlanResponse();

        response.setPlanId(plan.getPlanId());
        response.setTitle(plan.getTitle());
        response.setStartDate(plan.getStartDate());
        response.setExpectedEndDate(plan.getExpectedEndDate());
        response.setStatus(plan.getStatus());
        response.setReason(plan.getReason());
        response.setStagesDescription(plan.getStagesDescription());
        response.setCustomNotes(plan.getCustomNotes());

        if (plan.getUser() != null) {
            response.setUserId(plan.getUser().getUserId());
        }

        if (plan.getCoach() != null) {
            response.setCoachId(plan.getCoach().getUserId());
        }

        if (plan.getRecommendedPackage() != null) {
            response.setRecommendedPackageId(plan.getRecommendedPackage().getCigaretteId().intValue());
        }

        // Mapping stages và progress
        if (plan.getQuitPlanStages() != null && !plan.getQuitPlanStages().isEmpty()) {
            Set<QuitPlanStageResponse> stageResponses = plan.getQuitPlanStages().stream()
                    .map(stage -> {
                        QuitPlanStageResponse stageResp = new QuitPlanStageResponse();
                        stageResp.setStageId(stage.getStageId());
                        stageResp.setStageName(stage.getStageName());
                        stageResp.setStageStartDate(stage.getStageStartDate());
                        stageResp.setStageEndDate(stage.getStageEndDate());
                        stageResp.setTargetCigarettesPerDay(stage.getTargetCigarettesPerDay());
                        stageResp.setNotes(stage.getNotes());

                        if (stage.getQuitProgresses() != null && !stage.getQuitProgresses().isEmpty()) {
                            Set<QuitProgressResponse> progressResponses = stage.getQuitProgresses().stream()
                                    .map(progress -> {
                                        QuitProgressResponse pr = new QuitProgressResponse();
                                        pr.setProgressId(progress.getProgressId());
                                        pr.setDate(progress.getDate());
                                        pr.setCigarettesSmoked(progress.getCigarettesSmoked());
                                        pr.setMoneySpent(progress.getMoneySpent());
                                        pr.setMoneySaved(progress.getMoneySaved());
                                        pr.setSmokingFreeDays(progress.getSmokingFreeDays());
                                        pr.setHealthStatus(progress.getHealthStatus());
                                        return pr;
                                    })
                                    .collect(Collectors.toSet());

                            stageResp.setQuitProgresses(progressResponses);
                        }

                        return stageResp;
                    })
                    .collect(Collectors.toSet());

            response.setQuitPlanStages(stageResponses);
        }

        return response;
    }
}