package com.example.smoking_cessation_platform.dto.dashboard;


import com.example.smoking_cessation_platform.dto.dashboard.setup.TopPackage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageStatsResponse {
    private long totalPackages;        // Tổng số gói đang có trong hệ thống
    private long totalRegistered;      // Tổng số lần user đăng ký gói
    private List<TopPackage> topPackages; // Top gói được đăng ký nhiều nhất
}