package com.example.smoking_cessation_platform.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatsResponse {
    private double totalRevenue;                   // Tổng doanh thu
    private Map<String, Double> revenueByMethod;   // Doanh thu theo phương thức (VNPay, Cash...)
    private Map<String, Long> transactionByStatus; // Số lượng giao dịch theo trạng thái (SUCCESS, FAILED...)
}