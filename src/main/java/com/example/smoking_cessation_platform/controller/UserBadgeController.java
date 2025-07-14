package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.userbadge.UserBadgeRequest;
import com.example.smoking_cessation_platform.dto.userbadge.UserBadgeResponse;
import com.example.smoking_cessation_platform.service.UserBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user_badge")
@SecurityRequirement(name = "api")
public class UserBadgeController {


    @Autowired
    private UserBadgeService userBadgeService;

    /**
     * 1. Xem danh sách huy hiệu mình đã đạt được
     * GET /api/user_badges/user/{userId}
     */
    @Operation(summary = "Xem danh sách huy hiệu đã đạt", description = "Trả về danh sách huy hiệu mà người dùng đã đạt được.")
    @GetMapping("user/{userId}")
    public ResponseEntity<List<UserBadgeResponse>> getUserBadge(@PathVariable Long userId) {
        return ResponseEntity.ok(userBadgeService.getBadgesByUserId(userId));
    }

    /**
     * 2. Xem chi tiết từng huy hiệu
     * GET /api/user_badges/{userBadgeId}
     */
    @Operation(summary = "Xem chi tiết huy hiệu", description = "Trả về thông tin chi tiết của một huy hiệu người dùng đã đạt.")
    @GetMapping("/{userBadgeId}")
    public ResponseEntity<List<UserBadgeResponse>> getUserBadgeDetail(@PathVariable Integer userBadgeId) {
        return ResponseEntity.ok(userBadgeService.getBadgeById(userBadgeId));
    }

    /**
     * 3. Chia sẻ huy hiệu mình đã đạt
     * POST /api/user_badges/{id}/share
     */
    @Operation(summary = "Chia sẻ huy hiệu", description = "Cho phép người dùng chia sẻ huy hiệu mà họ đã đạt được.")
    @PostMapping("/{id}/share")
    public ResponseEntity<List<UserBadgeResponse>> shareUserBadge(@PathVariable Integer id) {
        return ResponseEntity.ok(userBadgeService.shareUserBadge(id));
    }

    /**
     * 4. Hủy chia sẻ huy hiệu
     * POST /api/user_badges/{id}/unshare
     */
    @Operation(summary = "Hủy chia sẻ huy hiệu", description = "Cho phép người dùng hủy chia sẻ một huy hiệu đã chia sẻ trước đó.")
    @PostMapping("/{id}/unshare")
    public ResponseEntity<List<UserBadgeResponse>> unshareUserBadge(@PathVariable Integer id) {
        return ResponseEntity.ok(userBadgeService.unshareUserBadge(id));
    }

    /**
     * 5. Xem huy hiệu người khác đã chia sẻ
     * GET /api/user_badges/share/user/{userId}
     */
    @Operation(summary = "Xem huy hiệu người khác chia sẻ", description = "Trả về danh sách huy hiệu đã chia sẻ bởi một người dùng cụ thể.")
    @GetMapping("/share/user/{userId}")
    public ResponseEntity<List<UserBadgeResponse>> getShareBadgeByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userBadgeService.getShareBadgesByUserId(userId));
    }
}
