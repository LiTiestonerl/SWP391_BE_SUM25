package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.dto.rating.RatingRequest;
import com.example.smoking_cessation_platform.dto.rating.RatingResponse;
import com.example.smoking_cessation_platform.entity.QuitPlan;
import com.example.smoking_cessation_platform.entity.Rating;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.mapper.RatingMapper;
import com.example.smoking_cessation_platform.repository.QuitPlanRepository;
import com.example.smoking_cessation_platform.repository.RatingRepository;
import com.example.smoking_cessation_platform.repository.UserMemberPackageRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

    @Autowired
    private UserRepository userRepository;


    /**
     * Tạo mới một đánh giá (rating) cho một kế hoạch đã hoàn thành.
     * - Chỉ người dùng sở hữu kế hoạch mới được đánh giá.
     * - Chỉ được đánh giá khi kế hoạch đã ở trạng thái COMPLETED.
     * - Chỉ đánh giá 1 lần duy nhất cho 1 kế hoạch.
     * - Nếu có coach, cần kiểm tra xem người dùng đã mua gói hỗ trợ từ coach đó chưa.
     */
    @Transactional
    public RatingResponse createRating(RatingRequest request) {
        // Lấy ID user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        // 1. Tìm kế hoạch theo ID
        QuitPlan plan = quitPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Kế hoạch không tồn tại."));

        // 2. Kiểm tra kế hoạch đã hoàn thành chưa
        if (plan.getStatus() != QuitPlanStatus.COMPLETED) {
            throw new IllegalStateException("Chỉ được đánh giá khi kế hoạch đã hoàn thành.");
        }

        // 3. Kiểm tra người gửi đánh giá có phải chủ kế hoạch không
        if (!plan.getUser().getUserId().equals(currentUserId)) {
            throw new SecurityException("Bạn không phải chủ kế hoạch này.");
        }

        // 4. Kiểm tra đã từng đánh giá kế hoạch này chưa
        if (ratingRepository.existsByMemberUserIdAndQuitPlanPlanId(currentUserId, request.getPlanId())) {
            throw new IllegalStateException("Bạn đã đánh giá kế hoạch này rồi.");
        }

        // 5. Lấy member và coach từ plan
        User member = plan.getUser();
        User coach = plan.getCoach();

        // 6. Nếu có coach, kiểm tra user có mua gói coach chưa
        if (coach != null) {
            boolean hasPackage = userMemberPackageRepository
                    .existsByUser_UserIdAndMemberPackage_SupportedCoaches_UserIdAndStatusIgnoreCase(
                            member.getUserId(), coach.getUserId(), "active");

            if (!hasPackage) {
                throw new IllegalStateException("Bạn chưa mua gói hỗ trợ từ coach này để đánh giá.");
            }
        }

        // 7. Tạo mới đối tượng Rating
        Rating rating = Rating.builder()
                .ratingValue(request.getRatingValue())
                .feedbackText(request.getFeedbackText())
                .ratingDate(LocalDateTime.now())
                .status("active")
                .member(member)
                .coach(coach)
                .quitPlan(plan)
                .build();

        // 8. Lưu vào cơ sở dữ liệu
        ratingRepository.save(rating);

        // 9. Trả về response
        return ratingMapper.toResponse(rating);
    }

    /**
     * Lấy danh sách đánh giá cho một huấn luyện viên cụ thể.
     */
    public List<RatingResponse> getRatingsByCoach(Long coachId) {
        return ratingRepository.findByCoachUserId(coachId)
                .stream().map(ratingMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách đánh giá mà một thành viên đã thực hiện.
     */
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        return ratingRepository.findByMemberUserId(memberId)
                .stream().map(ratingMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Lấy tất cả đánh giá liên quan đến một kế hoạch cụ thể.
     */
    public List<RatingResponse> getRatingsByPlan(Integer planId) {
        return ratingRepository.findByQuitPlanPlanId(planId)
                .stream().map(ratingMapper::toResponse).collect(Collectors.toList());
    }
}
