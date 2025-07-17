package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.dto.dashboard.*;
import com.example.smoking_cessation_platform.dto.dashboard.setup.TopBadge;
import com.example.smoking_cessation_platform.dto.dashboard.setup.TopPackage;
import com.example.smoking_cessation_platform.dto.dashboard.setup.TopPost;
import com.example.smoking_cessation_platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminStatsService {

    @Autowired
    private QuitPlanRepository quitPlanRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Autowired
    private MemberPackageRepository memberPackageRepository;
    @Autowired
    private UserBadgeRepository userBadgeRepository;

    public QuitPlanStatsResponse getQuitPlanStats() {
        long totalPlans = quitPlanRepository.count();
        long activePlans = quitPlanRepository.countByStatus(QuitPlanStatus.IN_PROGRESS);
        long completedPlans = quitPlanRepository.countByStatus(QuitPlanStatus.COMPLETED);
        long cancelPlans = quitPlanRepository.countByStatus(QuitPlanStatus.CANCELLED);

        double completionRate = (totalPlans > 0)
                ? (completedPlans * 100.0 / totalPlans)
                : 0.0;

        return new QuitPlanStatsResponse(
                totalPlans,
                activePlans,
                completedPlans,
                cancelPlans,
                completionRate
        );
    }

    public PostStatsResponse getPostStats() {
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();

        // Không có rating bài viết, giữ mặc định 0.0
        double averageRating = 0.0;

        // Lấy top bài viết nhiều comment nhất
        List<Object[]> topRaw = postRepository.findTopPosts();
        List<TopPost> topPosts = topRaw.stream()
                .map(row -> new TopPost(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();

        return new PostStatsResponse(
                totalPosts,
                totalComments,
                averageRating,
                topPosts
        );
    }

    public PaymentStatsResponse getPaymentStats() {
        double totalRevenue = paymentTransactionRepository.sumAmountByStatus("SUCCESS");

        Map<String, Double> revenueByMethod = new HashMap<>();
        List<Object[]> revenueList = paymentTransactionRepository.sumRevenueByMethod();
        for (Object[] row : revenueList) {
            String method = (String) row[0];
            Double amount = ((Number) row[1]).doubleValue();
            revenueByMethod.put(method, amount);
        }

        Map<String, Long> transactionByStatus = new HashMap<>();
        List<Object[]> statusList = paymentTransactionRepository.countTransactionByStatus();
        for (Object[] row : statusList) {
            String status = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            transactionByStatus.put(status, count);
        }

        return new PaymentStatsResponse(
                totalRevenue,
                revenueByMethod,
                transactionByStatus
        );
    }

    public PackageStatsResponse getPackageStats() {
        long totalPackages = memberPackageRepository.count();

        long totalRegistered = memberPackageRepository.countTotalRegistered();

        List<Object[]> topRaw = memberPackageRepository.findTopPackages();
        List<TopPackage> topPackages = topRaw.stream()
                .map(row -> new TopPackage(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();

        return new PackageStatsResponse(
                totalPackages,
                totalRegistered,
                topPackages
        );

    }


    public BadgeStatsResponse getBadgeStats() {
        long totalBadgesAwarded = userBadgeRepository.count(); // số huy hiệu đã trao

        List<Object[]> topRaw = userBadgeRepository.findTopBadges();
        List<TopBadge> topBadges = topRaw.stream()
                .map(row -> new TopBadge(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();

        return new BadgeStatsResponse(
                totalBadgesAwarded,
                topBadges
        );
    }
}

