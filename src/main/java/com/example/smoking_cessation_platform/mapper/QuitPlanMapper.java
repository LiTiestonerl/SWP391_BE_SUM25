package com.example.smoking_cessation_platform.mapper;

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

import lombok.RequiredArgsConstructor;
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

    /**
     * Chuyển từ QuitPlanRequest (DTO) sang QuitPlan entity.
     * Dùng khi tạo mới hoặc cập nhật kế hoạch cai thuốc.
     */
    public QuitPlan toEntity(QuitPlanRequest request) {
        QuitPlan quitPlan = new QuitPlan();

        // Gán thông tin cơ bản từ request
        quitPlan.setTitle(request.getTitle()); // ✅ Tiêu đề kế hoạch
        quitPlan.setStartDate(request.getStartDate());
        quitPlan.setExpectedEndDate(request.getExpectedEndDate());
        quitPlan.setStatus(request.getStatus());
        quitPlan.setReason(request.getReason());
        quitPlan.setStagesDescription(request.getStagesDescription());
        quitPlan.setCustomNotes(request.getCustomNotes());

        // Lấy thông tin người dùng (user) từ database
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        quitPlan.setUser(user);

        // Nếu có coach, tìm và gán
        if (request.getCoachId() != null) {
            User coach = userRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coach", request.getCoachId()));
            quitPlan.setCoach(coach);
        }

        // Nếu có gói thuốc lá đề xuất, tìm và gán
        if (request.getRecommendedPackageId() != null) {
            CigarettePackage pkg = packageRepository.findById(request.getRecommendedPackageId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("CigarettePackage", request.getRecommendedPackageId()));
            quitPlan.setRecommendedPackage(pkg);
        }

        return quitPlan;
    }

    /**
     * Chuyển từ QuitPlan entity sang QuitPlanResponse (DTO).
     * Dùng để trả dữ liệu ra phía client.
     */
    public QuitPlanResponse toResponse(QuitPlan plan) {
        QuitPlanResponse response = new QuitPlanResponse();

        // Gán các thông tin cơ bản
        response.setPlanId(plan.getPlanId());
        response.setTitle(plan.getTitle()); // ✅ Tiêu đề kế hoạch
        response.setStartDate(plan.getStartDate());
        response.setExpectedEndDate(plan.getExpectedEndDate());
        response.setStatus(plan.getStatus());
        response.setReason(plan.getReason());
        response.setStagesDescription(plan.getStagesDescription());
        response.setCustomNotes(plan.getCustomNotes());

        // Gán userId nếu có
        if (plan.getUser() != null) {
            response.setUserId(plan.getUser().getUserId());
        }

        // Gán coachId nếu có
        if (plan.getCoach() != null) {
            response.setCoachId(plan.getCoach().getUserId());
        }

        // Gán gói thuốc lá đề xuất nếu có
        if (plan.getRecommendedPackage() != null) {
            response.setRecommendedPackageId(plan.getRecommendedPackage().getCigaretteId().intValue());
        }

        // Nếu có các giai đoạn (stages), ánh xạ sang QuitPlanStageResponse
        if (plan.getQuitPlanStages() != null) {
            Set<QuitPlanStageResponse> stageResponses = plan.getQuitPlanStages().stream().map(stage -> {
                QuitPlanStageResponse stageResp = new QuitPlanStageResponse();
                stageResp.setStageId(stage.getStageId());
                stageResp.setStageName(stage.getStageName());
                stageResp.setStageStartDate(stage.getStageStartDate());
                stageResp.setStageEndDate(stage.getStageEndDate());
                stageResp.setTargetCigarettesPerDay(stage.getTargetCigarettesPerDay());
                stageResp.setNotes(stage.getNotes());

                // Nếu có tiến trình từng ngày trong giai đoạn, ánh xạ sang QuitProgressResponse
                if (stage.getQuitProgresses() != null) {
                    Set<QuitProgressResponse> progressResponses = stage.getQuitProgresses().stream().map(progress -> {
                        QuitProgressResponse pr = new QuitProgressResponse();
                        pr.setProgressId(progress.getProgressId());
                        pr.setDate(progress.getDate());
                        pr.setCigarettesSmoked(progress.getCigarettesSmoked());
                        pr.setMoneySpent(progress.getMoneySpent());
                        pr.setMoneySaved(progress.getMoneySaved());
                        pr.setSmokingFreeDays(progress.getSmokingFreeDays());
                        pr.setHealthStatus(progress.getHealthStatus());
                        return pr;
                    }).collect(Collectors.toSet());

                    stageResp.setQuitProgresses(progressResponses);
                }

                return stageResp;
            }).collect(Collectors.toSet());

            // Gán danh sách stage vào response
            response.setQuitPlanStages(stageResponses);
        }

        return response;
    }
}