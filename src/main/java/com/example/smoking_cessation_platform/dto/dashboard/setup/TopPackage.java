package com.example.smoking_cessation_platform.dto.dashboard.setup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopPackage {
    private Integer packageId;     // ID gói
    private String packageName; // Tên gói
    private long registerCount; // Số lần đăng ký
}
