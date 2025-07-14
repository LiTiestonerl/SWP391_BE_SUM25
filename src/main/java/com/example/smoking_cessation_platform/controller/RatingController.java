package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.rating.RatingRequest;
import com.example.smoking_cessation_platform.dto.rating.RatingResponse;
import com.example.smoking_cessation_platform.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
@SecurityRequirement(name = "api")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * API tạo đánh giá mới cho một kế hoạch đã hoàn thành (và coach của kế hoạch đó).
     * Chỉ cho phép đánh giá nếu:
     * - Kế hoạch có trạng thái COMPLETED
     * - Người gọi là chủ sở hữu của kế hoạch
     * - Người dùng chưa đánh giá kế hoạch này trước đó
     * - Người dùng đã mua gói hỗ trợ từ coach (nếu có coach)
     *
     * @param request đối tượng RatingRequest chứa thông tin đánh giá
     * @return RatingResponse chứa thông tin đánh giá đã lưu
     */
    @Operation(summary = "Tạo đánh giá cho kế hoạch đã hoàn thành")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<RatingResponse> create(@RequestBody RatingRequest request) {
        return ResponseEntity.ok(ratingService.createRating(request));
    }

    /**
     * API lấy tất cả đánh giá dành cho một coach cụ thể.
     * Dùng để coach xem feedback từ các user từng tham gia kế hoạch.
     *
     * @param coachId ID của coach
     * @return danh sách các đánh giá từ user dành cho coach này
     */
    @Operation(summary = "Lấy đánh giá dành cho coach")
    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<RatingResponse>> getByCoach(@PathVariable Long coachId) {
        return ResponseEntity.ok(ratingService.getRatingsByCoach(coachId));
    }

    /**
     * API lấy tất cả đánh giá mà một người dùng đã tạo.
     * Dùng cho user xem lại feedback mình đã gửi sau các kế hoạch.
     *
     * @param memberId ID của người dùng (user)
     * @return danh sách các đánh giá mà user đã gửi
     */
    @Operation(summary = "Lấy đánh giá do user đã gửi")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<RatingResponse>> getByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(ratingService.getRatingsByMember(memberId));
    }

    /**
     * API lấy tất cả đánh giá liên quan đến một kế hoạch cụ thể.
     * Dùng cho admin hoặc hệ thống hiển thị feedback liên quan đến một kế hoạch.
     *
     * @param planId ID của kế hoạch
     * @return danh sách các đánh giá liên quan đến kế hoạch đó
     */
    @Operation(summary = "Lấy đánh giá theo kế hoạch")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<RatingResponse>> getByPlan(@PathVariable Integer planId) {
        return ResponseEntity.ok(ratingService.getRatingsByPlan(planId));
    }
}
