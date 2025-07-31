package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.notification.NotificationRequest;
import com.example.smoking_cessation_platform.dto.notification.NotificationResponse;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import com.example.smoking_cessation_platform.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "api")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Tạo thông báo mới")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.create(request));
    }

    @Operation(summary = "Lấy danh sách thông báo theo user")
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getUserId(); // hoặc getUserId()
        return ResponseEntity.ok(notificationService.getByUser(currentUserId));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAsRead(id, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> delete(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.deleteByIdAndUser(id, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
