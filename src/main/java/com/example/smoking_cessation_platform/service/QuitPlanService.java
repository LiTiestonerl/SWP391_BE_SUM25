package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.Enum.NotificationStatus;
import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanRequest;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanResponse;
import com.example.smoking_cessation_platform.entity.*;
import com.example.smoking_cessation_platform.exception.BusinessException;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.mapper.QuitPlanMapper;
import com.example.smoking_cessation_platform.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuitPlanService {

    @Autowired
    private QuitPlanRepository quitPlanRepository;
    @Autowired
    private QuitPlanStageRepository quitPlanStageRepository;
    @Autowired
    private QuitProgressRepository quitProgressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CigarettePackageRepository cigarettePackageRepository;
    @Autowired
    private QuitPlanMapper quitPlanMapper;
    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private AchievementBadgeRepository achievementBadgeRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SmokingStatusRepository smokingStatusRepository;
    @Autowired
    private CigaretteRecommendationService cigaretteRecommendationService;
    @Autowired
    private CigaretteRecommendationRepository cigaretteRecommendationRepository;

    /**
     * Tạo mới một kế hoạch cai thuốc cho người dùng.
     * Tự động tạo các giai đoạn (stage) theo tuần và từng ngày (progress) trong mỗi giai đoạn.
     */
    @Transactional
    public QuitPlanResponse createPlan(QuitPlanRequest request) {

        // 1. Lấy thông tin user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole().getRoleName());

        // 2. Chỉ kiểm tra Smoking Status nếu KHÔNG phải ADMIN
        SmokingStatus smokingStatus = null;
        if (!isAdmin) {
            boolean hasSmokingStatus = smokingStatusRepository.existsByUser_UserId(request.getUserId());
            if (!hasSmokingStatus) {
                throw new BusinessException("Vui lòng cập nhật tình trạng hút thuốc (Smoking Status) trước khi tạo kế hoạch.");
            }

            // 3. USER không được có kế hoạch đang hoạt động
            quitPlanRepository.findFirstByUser_UserIdAndStatus(request.getUserId(), QuitPlanStatus.IN_PROGRESS)
                    .ifPresent(p -> {
                        throw new BusinessException("Bạn đã có một kế hoạch đang hoạt động.");
                    });

            // 4. Lấy Smoking Status mới nhất
            smokingStatus = smokingStatusRepository.findTopByUser_UserIdOrderByRecordDateDesc(request.getUserId())
                    .orElse(null);
        }

        // 5. Ánh xạ request -> entity và thiết lập thông tin ban đầu
        QuitPlan plan = quitPlanMapper.toEntity(request);
        plan.setStatus(QuitPlanStatus.IN_PROGRESS);
        plan.setTitle(request.getTitle());
        plan.setUser(user);

        // 6. Gán coach nếu có
        if (request.getCoachId() != null) {
            User coach = userRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coach", request.getCoachId()));

            if (!isAdmin) {
                boolean hasPackage = userMemberPackageRepository
                        .existsByUser_UserIdAndMemberPackage_SupportedCoaches_UserIdAndStatusIgnoreCase(
                                user.getUserId(), coach.getUserId(), "active");

                if (!hasPackage) {
                    throw new BusinessException("Bạn cần mua gói hỗ trợ huấn luyện viên này.");
                }
            }

            plan.setCoach(coach);
        }

        // 7. Gán gói thuốc được đề xuất nếu có sẵn
        List<CigarettePackage> recommendedPackages = new ArrayList<>();
        CigarettePackage recommendedPackage = null;

        if (request.getRecommendedPackageId() != null) {
            // Nếu người dùng đã chọn rõ gói thuốc thay thế
            recommendedPackage = cigarettePackageRepository.findById(request.getRecommendedPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cigarette Package", request.getRecommendedPackageId()));
        } else if (!isAdmin && smokingStatus != null && smokingStatus.getCigarettePackage() != null) {
            // Gợi ý tất cả các gói thuốc lá thay thế từ bảng recommendation (isActive = true)
            List<CigaretteRecommendation> recommendations = cigaretteRecommendationRepository
                    .findByFromPackage_CigaretteIdAndIsActiveTrue(smokingStatus.getCigarettePackage().getCigaretteId());

            recommendedPackages = recommendations.stream()
                    .map(CigaretteRecommendation::getToPackage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        plan.setRecommendedPackage(recommendedPackage);

        // 8. Lưu kế hoạch
        plan = quitPlanRepository.save(plan);

        // 9. Xác định cigarettesPerDay và pricePerPack
        Integer cigarettesPerDay = isAdmin
                ? request.getCigarettesPerDay()
                : smokingStatus != null ? smokingStatus.getCigarettesPerDay() : null;

        if (cigarettesPerDay == null || cigarettesPerDay <= 0) {
            cigarettesPerDay = 10; // fallback nếu thiếu
        }
        plan.setCigarettesPerDay(cigarettesPerDay);

        BigDecimal pricePerPack;

        if (recommendedPackage != null && recommendedPackage.getPrice() != null) {
            pricePerPack = recommendedPackage.getPrice();
        } else if (smokingStatus != null &&
                smokingStatus.getCigarettePackage() != null &&
                smokingStatus.getCigarettePackage().getPrice() != null) {
            pricePerPack = smokingStatus.getCigarettePackage().getPrice();
        } else {
            pricePerPack = BigDecimal.ZERO;
        }

        // 10. Tạo stage và progress
        List<QuitPlanStage> stages = generateStages(plan, cigarettesPerDay, pricePerPack);
        quitPlanStageRepository.saveAll(stages);

        for (QuitPlanStage stage : stages) {
            quitProgressRepository.saveAll(stage.getQuitProgresses());
        }

        plan.setQuitPlanStages(new HashSet<>(stages));

        // 11. Trả về response
        return quitPlanMapper.toResponse(plan);
    }


    /**
     * Tự động chia kế hoạch thành nhiều giai đoạn (stage), mỗi giai đoạn dài ~7 ngày.
     * Số điếu mục tiêu sẽ giảm dần theo từng giai đoạn.
     */
    private List<QuitPlanStage> generateStages(QuitPlan quitPlan, int initialCigarettesPerDay, BigDecimal pricePerPack) {
        List<QuitPlanStage> stages = new ArrayList<>();
        LocalDate start = quitPlan.getStartDate();
        LocalDate end = quitPlan.getExpectedEndDate();

        // Tính tổng số ngày của kế hoạch
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        // Mỗi stage dài 7 ngày → tính số stage cần tạo
        int numStages = (int) Math.ceil(totalDays / 7.0);

        for (int i = 0; i < numStages; i++) {
            // Tính ngày bắt đầu và kết thúc của từng stage
            LocalDate stageStart = start.plusDays(i * 7);
            LocalDate stageEnd = stageStart.plusDays(6);
            if (stageEnd.isAfter(end)) stageEnd = end; // Không vượt quá ngày kết thúc kế hoạch

            // Giảm dần số điếu thuốc mục tiêu theo từng stage
            double ratio = 1.0 - ((double)i / (double)numStages);
            int target = (int) Math.ceil(initialCigarettesPerDay * ratio);

            QuitPlanStage stage = new QuitPlanStage();
            stage.setStageName("Giai đoạn " + (i + 1));
            stage.setStageStartDate(stageStart);
            stage.setStageEndDate(stageEnd);
            stage.setTargetCigarettesPerDay(target);
            stage.setNotes("Giai đoạn " + (i + 1));
            stage.setQuitPlan(quitPlan);

            // Tạo progress theo từng ngày trong giai đoạn và truyền stage vào
            stage.setQuitProgresses(new HashSet<>(generateProgresses(stageStart, stageEnd, pricePerPack, stage)));

            stages.add(stage);
        }

        return stages;
    }

    /**
     * Tạo các bản ghi QuitProgress cho mỗi ngày từ startDate đến endDate.
     * Các giá trị mặc định sẽ là 0 và status là “Chưa cập nhật”.
     */
    private List<QuitProgress> generateProgresses(LocalDate startDate, LocalDate endDate, BigDecimal pricePerPack, QuitPlanStage stage) {
        List<QuitProgress> progresses = new ArrayList<>();
        LocalDate current = startDate;

        int cigarettesPerPack = 20; // Giả định 1 bao thuốc = 20 điếu
        BigDecimal pricePerCigarette = pricePerPack.divide(BigDecimal.valueOf(cigarettesPerPack), 2, RoundingMode.HALF_UP);

        int expectedCigarettesPerDay = stage.getTargetCigarettesPerDay() != null ? stage.getTargetCigarettesPerDay() : 0;

        while (!current.isAfter(endDate)) {
            QuitProgress progress = new QuitProgress();
            progress.setDate(current);
            progress.setCigarettesSmoked(0); // User sẽ cập nhật sau

            // Lúc khởi tạo, user chưa hút gì, nên moneySpent = 0
            progress.setMoneySpent(BigDecimal.ZERO);

            // Giả định ban đầu tiết kiệm được toàn bộ số tiền theo kế hoạch
            BigDecimal expectedSpent = pricePerCigarette.multiply(BigDecimal.valueOf(expectedCigarettesPerDay));
            progress.setMoneySaved(expectedSpent);

            progress.setSmokingFreeDays(0); // Sẽ được tính lại khi user cập nhật
            progress.setHealthStatus(null); // User sẽ nhập thủ công
            progress.setQuitPlanStage(stage);

            progresses.add(progress);
            current = current.plusDays(1);
        }

        return progresses;
    }


    // 2. Lấy chi tiết kế hoạch (nếu có coach thì kiểm tra user đã mua gói phù hợp chưa)
    public QuitPlanResponse getPlanById(Integer planId) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", planId));

        if (plan.getCoach() != null) {
            boolean hasAccess = userMemberPackageRepository.existsByUser_UserIdAndMemberPackage_SupportedCoaches_UserIdAndStatusIgnoreCase(
                    plan.getUser().getUserId(),
                    plan.getCoach().getUserId(),
                    "active");

            if (!hasAccess) {
                throw new RuntimeException("Bạn chưa đăng ký gói thành viên hỗ trợ huấn luyện viên này.");
            }
        }

        Set<QuitPlanStage> stages = quitPlanStageRepository.findByQuitPlan_PlanId(planId);
        for (QuitPlanStage stage : stages) {
            Set<QuitProgress> progresses = quitProgressRepository.findByQuitPlanStage_StageId(stage.getStageId());
            stage.setQuitProgresses(progresses);
        }
        plan.setQuitPlanStages(stages);

        return quitPlanMapper.toResponse(plan);
    }

    // 3. Lấy danh sách kế hoạch theo user
    public List<QuitPlanResponse> getPlansByUser(Long userId) {
        return quitPlanRepository.findByUser_UserId(userId)
                .stream()
                .map(quitPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 4. Coach cập nhật kế hoạch (nếu user đã mua gói hỗ trợ coach này)
    public QuitPlanResponse updatePlanByCoach(Integer planId, QuitPlanRequest request) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", planId));

        boolean canEdit = userMemberPackageRepository.existsByUser_UserIdAndMemberPackage_SupportedCoaches_UserIdAndStatusIgnoreCase(
                plan.getUser().getUserId(),
                plan.getCoach().getUserId(),
                "active");

        if (!canEdit) {
            throw new RuntimeException("Coach không có quyền chỉnh sửa kế hoạch này.");
        }

        plan.setTitle(request.getTitle());
        plan.setExpectedEndDate(request.getExpectedEndDate());
        plan.setStagesDescription(request.getStagesDescription());
        plan.setCustomNotes(request.getCustomNotes());

        return quitPlanMapper.toResponse(quitPlanRepository.save(plan));
    }

    // 5. User cập nhật kế hoạch của mình
    public QuitPlanResponse updatePlanByUser(Integer planId, QuitPlanRequest request) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", planId));

        plan.setTitle(request.getTitle());
        plan.setReason(request.getReason());
        plan.setExpectedEndDate(request.getExpectedEndDate());
        plan.setCustomNotes(request.getCustomNotes());

        return quitPlanMapper.toResponse(quitPlanRepository.save(plan));
    }

    // 6. User xoá kế hoạch của mình
    public void deletePlanByUser(Integer planId, Long userId) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", planId));

        if (!plan.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa kế hoạch này.");
        }

        quitPlanRepository.delete(plan);
    }

    // 7. Lấy danh sách kế hoạch miễn phí (chưa có coach)
    public List<QuitPlanResponse> getFreePlans() {
        return quitPlanRepository.findByCoachIsNull()
                .stream()
                .map(quitPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 8. Lấy kế hoạch đang active
    public QuitPlanResponse getCurrentActivePlan(Long userId) {
        QuitPlan plan = quitPlanRepository.findFirstByUser_UserIdAndStatus(userId, QuitPlanStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("Active QuitPlan for user", userId));
        return quitPlanMapper.toResponse(plan);
    }

    // 9. Hủy kế hoạch đang active (nếu có lý do thì ghi lại)
    public QuitPlanResponse cancelPlan(Integer planId, String reason) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", planId));

        plan.setStatus(QuitPlanStatus.CANCELLED);
        if (reason != null) plan.setCustomNotes(reason);

        return quitPlanMapper.toResponse(quitPlanRepository.save(plan));
    }

    // 10. Đánh dấu hoàn thành kế hoạch
    public QuitPlanResponse completePlan(Integer planId) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan", planId));

        // Nếu kế hoạch đã hoàn thành thì không làm gì cả
        if (plan.getStatus() == QuitPlanStatus.COMPLETED) {
            return quitPlanMapper.toResponse(plan);
        }

        // ✅ Cập nhật trạng thái kế hoạch
        plan.setStatus(QuitPlanStatus.COMPLETED);
        quitPlanRepository.save(plan);

        User user = plan.getUser();

        // 🎖️ Kiểm tra số kế hoạch đã hoàn thành
        int completedPlans = quitPlanRepository.countByUserAndStatus(user, QuitPlanStatus.COMPLETED);

        // Tặng huy hiệu theo mức độ
        if (completedPlans >= 5) {
            grantBadgeIfNotExists(user, "Hoàn thành 5 kế hoạch cai thuốc", plan);
        } else if (completedPlans >= 3) {
            grantBadgeIfNotExists(user, "Hoàn thành 3 kế hoạch cai thuốc", plan);
        } else if (completedPlans >= 1) {
            grantBadgeIfNotExists(user, "Hoàn thành 1 kế hoạch cai thuốc", plan);
        }

        return quitPlanMapper.toResponse(plan);
    }

    private void grantBadgeIfNotExists(User user, String badgeName, QuitPlan plan) {
        //  Tìm huy hiệu theo tên
        AchievementBadge badge = achievementBadgeRepository.findByBadgeName(badgeName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy huy hiệu: " + badgeName));

        // Kiểm tra người dùng đã nhận huy hiệu này chưa
        boolean alreadyAwarded = userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(
                user.getUserId(), badge.getBadgeId()
        );

        if (!alreadyAwarded) {
            // Tạo và lưu UserBadge
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setShared(false);
            userBadgeRepository.save(userBadge);

            //  Tạo và lưu Notification
            Notification notify = Notification.builder()
                    .user(user)
                    .content("Bạn đã hoàn thành kế hoạch và nhận được huy hiệu: " + badgeName)
                    .notificationType("QUIT_PLAN_COMPLETED")
                    .sendDate(LocalDateTime.now())
                    .status(NotificationStatus.SENT)
                    .deleted(false)
                    .quitPlan(plan)
                    .achievementBadge(badge)
                    .build();

            notificationRepository.save(notify);
        }
    }
}


