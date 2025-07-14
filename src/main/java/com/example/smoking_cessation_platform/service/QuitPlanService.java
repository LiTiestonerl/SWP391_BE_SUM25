package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanRequest;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanResponse;
import com.example.smoking_cessation_platform.entity.*;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * Tạo mới một kế hoạch cai thuốc cho người dùng.
     * Tự động tạo các giai đoạn (stage) theo tuần và từng ngày (progress) trong mỗi giai đoạn.
     */
    @Transactional
    public QuitPlanResponse createPlan(QuitPlanRequest request) {
        // Ánh xạ request thành thực thể QuitPlan và set trạng thái ban đầu là IN_PROGRESS
        QuitPlan plan = quitPlanMapper.toEntity(request);
        plan.setStatus(QuitPlanStatus.IN_PROGRESS);
        plan.setTitle(request.getTitle());
        plan = quitPlanRepository.save(plan);

        // ✅ Lấy thông tin hút thuốc gần nhất của người dùng từ bảng SmokingStatus
        SmokingStatus smokingStatus = smokingStatusRepository
                .findTopByUser_UserIdOrderByRecordDateDesc(request.getUserId())
                .orElse(null);

        // Nếu có dữ liệu thì lấy số điếu/ngày, ngược lại gán mặc định là 10 điếu/ngày
        int initialCigarettesPerDay = (smokingStatus != null && smokingStatus.getCigarettesPerDay() != null)
                ? smokingStatus.getCigarettesPerDay()
                : 10;

        // Lấy giá mỗi bao thuốc (để sau này tính tiền tiết kiệm), nếu không có thì mặc định là 0
        BigDecimal pricePerPack = (smokingStatus != null) ? smokingStatus.getPricePerPack() : BigDecimal.ZERO;

        // ✅ Tạo các stage và progress tương ứng theo kế hoạch
        List<QuitPlanStage> stages = generateStages(plan, initialCigarettesPerDay, pricePerPack);

        // Lưu stage và progress vào database
        for (QuitPlanStage stage : stages) {
            stage.setQuitPlan(plan);
            quitPlanStageRepository.save(stage);

            for (QuitProgress progress : stage.getQuitProgresses()) {
                progress.setQuitPlanStage(stage);
                quitProgressRepository.save(progress);
            }
        }

        // Gán các stage cho kế hoạch để trả response
        plan.setQuitPlanStages(new HashSet<>(stages));

        // 6. Trả về response
        QuitPlanResponse response = quitPlanMapper.toResponse(plan);

        // ✅ 7. Gợi ý gói thuốc có nicotine thấp hơn nếu có thông tin gói hiện tại
        if (smokingStatus != null && smokingStatus.getCigarettePackage() != null) {
            Long currentPackageId = smokingStatus.getCigarettePackage().getCigaretteId();

            // Gọi service gợi ý
            List<CigarettePackageDTO> suggestedPackages =
                    cigaretteRecommendationService.suggestLowerNicotinePackages(currentPackageId);

            // Gán vào response (cần khai báo trường nicotineSuggestions trong DTO)
            response.setNicotineSuggestions(suggestedPackages);
        }

        return response;
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
            int target = Math.max(0, initialCigarettesPerDay - (initialCigarettesPerDay * i / numStages));

            QuitPlanStage stage = new QuitPlanStage();
            stage.setStageName("Giai đoạn " + (i + 1));
            stage.setStageStartDate(stageStart);
            stage.setStageEndDate(stageEnd);
            stage.setTargetCigarettesPerDay(target);
            stage.setNotes("Giai đoạn " + (i + 1));
            stage.setQuitPlan(quitPlan);

            // Tạo progress theo từng ngày trong giai đoạn
            stage.setQuitProgresses(new HashSet<>(generateProgresses(stageStart, stageEnd, pricePerPack)));

            stages.add(stage);
        }

        return stages;
    }

    /**
     * Tạo các bản ghi QuitProgress cho mỗi ngày từ startDate đến endDate.
     * Các giá trị mặc định sẽ là 0 và status là “Chưa cập nhật”.
     */
    private List<QuitProgress> generateProgresses(LocalDate startDate, LocalDate endDate, BigDecimal pricePerPack) {
        List<QuitProgress> progresses = new ArrayList<>();
        LocalDate current = startDate;

        int cigarettesPerPack = 20; // Giả định 1 bao thuốc = 20 điếu

        // Tính giá cho 1 điếu thuốc (để sau này tính tiết kiệm)
        BigDecimal pricePerCigarette = pricePerPack.divide(BigDecimal.valueOf(cigarettesPerPack), 2, RoundingMode.HALF_UP);

        while (!current.isAfter(endDate)) {
            QuitProgress progress = new QuitProgress();
            progress.setDate(current);
            progress.setCigarettesSmoked(0); // ban đầu chưa hút
            progress.setMoneySpent(BigDecimal.ZERO); // ban đầu chưa tốn tiền
            progress.setMoneySaved(BigDecimal.ZERO); // sẽ cập nhật sau
            progress.setSmokingFreeDays(0); // chưa có ngày nào không hút
            progress.setHealthStatus("Chưa cập nhật"); // người dùng sẽ cập nhật sau

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

        // Cập nhật trạng thái kế hoạch
        plan.setStatus(QuitPlanStatus.COMPLETED);
        quitPlanRepository.save(plan);

        // 🎖️ Tặng huy hiệu nếu chưa có
        AchievementBadge badge = achievementBadgeRepository.findByBadgeType("QUIT_PLAN_COMPLETED")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy huy hiệu tương ứng."));

        boolean alreadyAwarded = userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(
                plan.getUser().getUserId(),
                badge.getBadgeId()
        );

        if (!alreadyAwarded) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(plan.getUser());
            userBadge.setBadge(badge);
            userBadge.setShared(false); // Không chia sẻ mặc định
            userBadgeRepository.save(userBadge);
        }

        // 🔔 Gửi thông báo
        Notification notify = new Notification();
        notify.setUser(plan.getUser());
        notify.setContent("Bạn đã hoàn thành kế hoạch cai thuốc và nhận được huy hiệu: " + badge.getBadgeName());
        notify.setNotificationType("QUIT_PLAN_COMPLETED");
        notify.setSendDate(LocalDateTime.now());
        notify.setStatus("sent");
        notify.setDeleted(false); // Mặc định chưa xoá
        notify.setQuitPlan(plan);
        notify.setAchievementBadge(badge);

        notificationRepository.save(notify);

        return quitPlanMapper.toResponse(plan);
    }
}


