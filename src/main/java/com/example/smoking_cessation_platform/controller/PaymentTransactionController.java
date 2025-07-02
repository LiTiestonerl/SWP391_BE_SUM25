package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.paymentTransaction.PaymentRequest;
import com.example.smoking_cessation_platform.service.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@SecurityRequirement(name = "api")
public class PaymentTransactionController {

    @Autowired
    PaymentTransactionService paymentTransactionService;

    /**
     * B1: FE gọi API này để tạo URL thanh toán cho một gói thành viên.
     * B2: Server sẽ tạo bản ghi PaymentTransaction (status = "PENDING"),
     *     sinh URL VNPay và trả lại cho FE để redirect người dùng.
     */

    @PostMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> createPaymentRequest (@RequestBody PaymentRequest paymentRequest){
        String paymentUrl = paymentTransactionService.createPaymentUrl(paymentRequest);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    /**
     * B3: VNPay redirect về URL này sau khi thanh toán.
     * Server sẽ:
     * 1. Kiểm tra chữ ký (vnp_SecureHash).
     * 2. Nếu hợp lệ thì update transaction (PENDING -> SUCCESS / FAILED).
     * 3. Trả thông báo thành công/thất bại về phía người dùng.
     */
    @GetMapping("/vnpay-return")
    @PermitAll
    @Operation(summary = "VNPay Callback", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<String> handleVnPayReturn(HttpServletRequest request) {
        String resultMessage = paymentTransactionService.processVnPayReturn(request);
        return ResponseEntity.ok(resultMessage);
    }
}
