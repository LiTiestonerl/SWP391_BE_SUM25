package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.paymentTransaction.PaymentRequest;
import com.example.smoking_cessation_platform.entity.PaymentTransaction;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@SecurityRequirement(name = "api")
public class PaymentTransactionController {

    @Autowired
    PaymentTransactionService paymentTransactionService;

    /**
     * [B1] Tạo URL thanh toán VNPay cho một gói thành viên
     *
     * - FE gửi PaymentRequest (chứa memberPackageId).
     * - Server sẽ:
     *    + Kiểm tra gói thành viên.
     *    + Tạo transaction (status = "PENDING").
     *    + Sinh URL thanh toán của VNPay (có kèm vnp_SecureHash).
     *    + Trả URL về cho FE để redirect người dùng đến VNPay.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> createPaymentRequest(@RequestBody PaymentRequest paymentRequest) {
        String paymentUrl = paymentTransactionService.createPaymentUrl(paymentRequest);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    /**
     * [B2] VNPay redirect người dùng về đây sau khi thanh toán xong.
     *
     * - Đây là endpoint BE dùng làm vnp_ReturnUrl.
     * - Nhưng BE KHÔNG xử lý xác thực giao dịch ở đây vì:
     *    + Không có access token (không thể xác định user).
     *    + Không đảm bảo tính bảo mật nếu xử lý khi chưa xác thực.
     * - FE sẽ nhận URL này, lấy `txnRef` từ query param rồi gọi tiếp `/api/payment/status`.
     */
    @GetMapping("/vnpay-return")
    @PermitAll
    @Operation(summary = "VNPay Callback", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<?> handleVnPayReturn(HttpServletRequest request) {
        // Trả lời đơn giản để VNPay redirect không bị lỗi
        return ResponseEntity.ok("Redirect handled. FE should call /api/payment/status.");
    }

    /**
     * [B3] FE xác thực kết quả thanh toán sau redirect
     *
     * - Sau khi người dùng thanh toán xong, FE lấy `txnRef` từ URL
     *   rồi gọi endpoint này kèm token để xác thực giao dịch.
     * - BE sẽ:
     *    + Tìm giao dịch theo txnRef.
     *    + Kiểm tra trạng thái PENDING.
     *    + Xác thực giao dịch từ VNPay (bằng cách validate các tham số hoặc trạng thái).
     *    + Nếu SUCCESS thì cập nhật trạng thái và cấp gói.
     * - Trả lại thông tin giao dịch cho FE.
     */
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> checkPaymentStatus(@RequestParam String txnRef) {
        Optional<PaymentTransaction> txOpt = paymentTransactionService.verifyAndProcessTransaction(txnRef);
        if (txOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Giao dịch không hợp lệ hoặc đã xử lý."));
        }

        PaymentTransaction tx = txOpt.get();
        return ResponseEntity.ok(Map.of(
                "txnRef", tx.getTxnRef(),
                "status", tx.getStatus(),
                "amount", tx.getAmount(),
                "package", tx.getMemberPackage().getPackageName(),
                "transactionDate", tx.getTransactionDate()
        ));
    }
}
