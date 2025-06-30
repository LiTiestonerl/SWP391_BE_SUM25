package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.paymentTransaction.PaymentRequest;
import com.example.smoking_cessation_platform.entity.MemberPackage;
import com.example.smoking_cessation_platform.entity.PaymentTransaction;
import com.example.smoking_cessation_platform.repository.MemberPackageRepository;
import com.example.smoking_cessation_platform.repository.PaymentTransactionRepository;
import com.example.smoking_cessation_platform.vnpay.VnPayProperties;
import com.example.smoking_cessation_platform.vnpay.VnPayUtils;
import com.google.api.client.util.Value;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service
public class PaymentTransactionService {

    @Autowired
    MemberPackageRepository memberPackageRepository;

    @Autowired
    VnPayProperties vnPayProperties;

    @Autowired
    UserService userService;


    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;

    @Value("${integration.vnpay.secret-key}")
    private String hashSecret;

    public String createPaymentUrl(PaymentRequest paymentRequest) {
        // 1. Lấy thông tin gói thành viên từ DB
        MemberPackage memberPackage = memberPackageRepository.findById(paymentRequest.getMemberPackageId())
                .orElseThrow(() -> new RuntimeException("Gói thành viên không tồn tại"));

        String orderId = VnPayUtils.getRandomNumber(8); // Mã giao dịch ngẫu nhiên
        String currCode = "VND";
        String clientIp = "167.99.74.201";

        // 2. Format thời gian
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedCreateDate = now.format(formatter);

        //3 tạo vnParams
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", vnPayProperties.getVersion());
        vnpParams.put("vnp_Command", vnPayProperties.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Thanh toán cho gói: " + memberPackage.getMemberPackageId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", memberPackage.getPrice().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        vnpParams.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", clientIp);

        // 4. Sinh chuỗi query + secure hash
        String query = VnPayUtils.getPaymentURL(vnpParams, true);
        String secureHash = VnPayUtils.hmacSHA512(vnPayProperties.getSecretKey(), query);

        // 5. Lưu PaymentTransaction (nếu cần)
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTxnRef(orderId);
        transaction.setAmount(memberPackage.getPrice());
        transaction.setCurrency(currCode);
        transaction.setTransactionDate(now);
        transaction.setPaymentMethod("VNPay");
        transaction.setStatus("PENDING");
        transaction.setMemberPackage(memberPackage);
        transaction.setUser(userService.getCurrentUser());

        paymentTransactionRepository.save(transaction);

        // 6. Trả về full URL redirect đến VNPay
        return vnPayProperties.getUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    public String processVnPayReturn(HttpServletRequest request) {
        Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()[0],  // Lấy phần tử đầu
                        (oldVal, newVal) -> oldVal, // Tránh lỗi duplicate key
                        TreeMap::new // Sắp xếp theo alphabet cho chắc
                ));

        // ✅ Validate chữ ký
        boolean isValidSignature = VnPayUtils.validateSignature(vnpParams, hashSecret);
        if (!isValidSignature) {
            return " Chữ ký không hợp lệ. Có thể bị giả mạo!";
        }

        // ✅ Lấy txnRef để tìm transaction
        String txnRef = vnpParams.get("vnp_TxnRef");
        Optional<PaymentTransaction> optTx = paymentTransactionRepository.findByTxnRef(txnRef);

        if (optTx.isEmpty()) {
            return " Không tìm thấy giao dịch với mã tham chiếu: " + txnRef;
        }

        PaymentTransaction tx = optTx.get();

        // ✅ Kiểm tra response code từ VNPay
        String responseCode = vnpParams.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            tx.setStatus("SUCCESS");
        } else {
            tx.setStatus("FAILED");
        }

        // ✅ Cập nhật thêm thông tin từ VNPay
        tx.setTransactionCode(vnpParams.get("vnp_TransactionNo"));
        tx.setTransactionDate(LocalDateTime.now());

        paymentTransactionRepository.save(tx);

        // ✅ Trả kết quả cho người dùng
        return " Thanh toán " + (responseCode.equals("00") ? "thành công" : "không thành công") +
                " với mã giao dịch: " + txnRef;
    }
}

