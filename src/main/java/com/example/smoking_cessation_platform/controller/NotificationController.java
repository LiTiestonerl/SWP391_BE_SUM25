package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.notification.NotificationRequest;
import com.example.smoking_cessation_platform.dto.notification.NotificationResponse;
import com.example.smoking_cessation_platform.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.create(request));
    }
    @Operation(summary = "Lấy danh sách thông báo theo user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }
}
