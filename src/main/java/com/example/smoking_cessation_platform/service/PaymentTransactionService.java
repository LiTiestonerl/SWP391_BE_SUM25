package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.paymentTransaction.PaymentRequest;
import com.example.smoking_cessation_platform.entity.MemberPackage;
import com.example.smoking_cessation_platform.entity.PaymentTransaction;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.UserMemberPackage;
import com.example.smoking_cessation_platform.repository.MemberPackageRepository;
import com.example.smoking_cessation_platform.repository.PaymentTransactionRepository;
import com.example.smoking_cessation_platform.repository.UserMemberPackageRepository;
import com.example.smoking_cessation_platform.vnpay.VnPayProperties;
import com.example.smoking_cessation_platform.vnpay.VnPayUtils;
import com.google.api.client.util.Value;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service
public class PaymentTransactionService {

    @Autowired
    private MemberPackageRepository memberPackageRepository;

    @Autowired
    private VnPayProperties vnPayProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

    @Autowired
    private MemberPackageService memberPackageService;

    @Value("${integration.vnpay.secret-key}")
    private String hashSecret;

    public String createPaymentUrl(PaymentRequest paymentRequest) {
        MemberPackage memberPackage = memberPackageRepository.findById(paymentRequest.getMemberPackageId())
                .orElseThrow(() -> new RuntimeException("Gói thành viên không tồn tại"));

        String orderId = VnPayUtils.getRandomNumber(8);
        String currCode = "VND";
        String clientIp = "167.99.74.201";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedCreateDate = now.format(formatter);

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

        String query = VnPayUtils.getPaymentURL(vnpParams, true);
        String secureHash = VnPayUtils.hmacSHA512(vnPayProperties.getSecretKey(), query);

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

        return vnPayProperties.getUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }


    public Optional<PaymentTransaction> verifyAndProcessTransaction(String txnRef, String responseCode) {
        PaymentTransaction tx = paymentTransactionRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với mã: " + txnRef));

        // Nếu không phải PENDING, trả lại luôn giao dịch hiện tại (để FE biết đã xử lý rồi)
        if (!"PENDING".equalsIgnoreCase(tx.getStatus())) {
            return Optional.of(tx);
        }

        // Nếu thanh toán thất bại
        if (!"00".equals(responseCode)) {
            tx.setStatus("FAILED");
            tx.setTransactionDate(LocalDateTime.now());
            paymentTransactionRepository.save(tx);
            return Optional.of(tx);
        }

        // Thanh toán thành công
        tx.setStatus("SUCCESS");
        tx.setTransactionDate(LocalDateTime.now());
        paymentTransactionRepository.save(tx);

        memberPackageService.grantMemberPackage(tx.getUser(), tx.getMemberPackage());

        return Optional.of(tx);
    }
}


