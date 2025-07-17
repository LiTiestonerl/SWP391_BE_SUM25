package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.dashboard.*;

import com.example.smoking_cessation_platform.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {
    @Autowired
    private AdminStatsService adminStatsService;

    @Operation(summary = "Thống kê kế hoạch")
    @GetMapping("/quitplans")
    public QuitPlanStatsResponse getQuitPlanStats() {
        return adminStatsService.getQuitPlanStats();
    }

    @Operation(summary = "Thống kê bài viết")
    @GetMapping("/posts")
    public PostStatsResponse getPostStats() {
        return adminStatsService.getPostStats();
    }

    @Operation(summary = "Thống kê thanh toán")
    @GetMapping("/payments")
    public PaymentStatsResponse getPaymentStats() {
        return adminStatsService.getPaymentStats();
    }

    @Operation(summary = "Thống kê gói dịch vụ")
    @GetMapping("/packages")
    public PackageStatsResponse getPackageStats() {
        return adminStatsService.getPackageStats();
    }

    @Operation(summary = "Thống kê huy hiệu")
    @GetMapping("/badges")
    public BadgeStatsResponse getBadgeStats() {
        return adminStatsService.getBadgeStats();
    }
}
