package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanRequest;
import com.example.smoking_cessation_platform.dto.quitplan.QuitPlanResponse;
import com.example.smoking_cessation_platform.service.QuitPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quit-plan")
@SecurityRequirement(name = "api")
public class QuitPlanController {
    @Autowired
    private QuitPlanService quitPlanService;

    /**
     * 1. User tạo kế hoạch cai thuốc
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Tạo kế hoạch cai thuốc", description = "Người dùng tạo một kế hoạch mới để cai thuốc.")
    @PostMapping
    public ResponseEntity<QuitPlanResponse> createQuitPlan(@RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = quitPlanService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. Xem chi tiết kế hoạch
     */
    @PreAuthorize("hasAnyRole('USER', 'COACH', 'ADMIN')")
    @Operation(summary = "Xem chi tiết kế hoạch", description = "Xem thông tin chi tiết của một kế hoạch theo ID.")
    @GetMapping("/{planId}")
    public ResponseEntity<QuitPlanResponse> getQuitPlan(@PathVariable Integer planId) {
        QuitPlanResponse response = quitPlanService.getPlanById(planId);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Xem danh sách kế hoạch theo user
     */
    @PreAuthorize("#userId == authentication.principal.userId or hasAnyRole('COACH')")
    @Operation(summary = "Lấy danh sách kế hoạch theo người dùng", description = "Lấy tất cả kế hoạch cai thuốc của một người dùng cụ thể.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuitPlanResponse>> getPlansByUser(@PathVariable Long userId) {
        List<QuitPlanResponse> responses = quitPlanService.getPlansByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 4. Coach cập nhật kế hoạch nếu là member
     */
    @PreAuthorize("hasAnyRole('COACH')")
    @Operation(summary = "Coach cập nhật kế hoạch", description = "Huấn luyện viên cập nhật kế hoạch nếu người dùng đã mua gói hỗ trợ.")
    @PutMapping("/{planId}/coach")
    public ResponseEntity<QuitPlanResponse> updateQuitPlanByCoach(
            @PathVariable Integer planId,
            @RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = quitPlanService.updatePlanByCoach(planId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. Người dùng cập nhật kế hoạch của chính mình
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Người dùng cập nhật kế hoạch", description = "Cho phép người dùng chỉnh sửa kế hoạch của chính họ.")
    @PutMapping("/{planId}/user")
    public ResponseEntity<QuitPlanResponse> updateQuitPlanByUser(
            @PathVariable Integer planId,
            @RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = quitPlanService.updatePlanByUser(planId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 6. Người dùng xóa kế hoạch của chính mình
     */
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    @Operation(summary = "Xóa kế hoạch", description = "Người dùng có thể xóa một kế hoạch thuộc quyền sở hữu của họ.")
    @DeleteMapping("/{planId}/user/{userId}")
    public ResponseEntity<Void> deleteQuitPlan(@PathVariable Integer planId, @PathVariable Long userId) {
        quitPlanService.deletePlanByUser(planId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7. Lấy danh sách kế hoạch miễn phí
     */
    @Operation(summary = "Lấy kế hoạch miễn phí", description = "Lấy danh sách các kế hoạch cai thuốc miễn phí.")
    @GetMapping("/free")
    public ResponseEntity<List<QuitPlanResponse>> getFreePlans() {
        return ResponseEntity.ok(quitPlanService.getFreePlans());
    }

    /**
     * 8. Lấy kế hoạch đang active của người dùng
     */
    @PreAuthorize("#userId == authentication.principal.userId")
    @Operation(summary = "Lấy kế hoạch đang hoạt động", description = "Trả về kế hoạch đang active hiện tại của người dùng (nếu có).")
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<QuitPlanResponse> getCurrentActivePlan(@PathVariable Long userId) {
        QuitPlanResponse response = quitPlanService.getCurrentActivePlan(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 9. Hủy kế hoạch đang active (do người dùng yêu cầu)
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Hủy kế hoạch đang hoạt động", description = "Người dùng yêu cầu hủy kế hoạch đang active.")
    @PutMapping("/{planId}/cancel")
    public ResponseEntity<QuitPlanResponse> cancelActivePlan(
            @PathVariable Integer planId,
            @RequestParam(required = false) String reason) {
        QuitPlanResponse response = quitPlanService.cancelPlan(planId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * 10. Hoàn thành kế hoạch
     */
    @Operation(summary = "Hoàn thành kế hoạch", description = "Đánh dấu kế hoạch là đã hoàn thành và tặng huy hiệu cho người dùng.")
    @PutMapping("/{planId}/complete")
    public ResponseEntity<QuitPlanResponse> completePlan(@PathVariable Integer planId) {
        QuitPlanResponse response = quitPlanService.completePlan(planId);
        return ResponseEntity.ok(response);
    }
}
