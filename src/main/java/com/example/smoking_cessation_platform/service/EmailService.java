package com.example.smoking_cessation_platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("tiennmse173628@fpt.edu.vn");
            message.setTo(toEmail);
            message.setSubject("Mã Xác Thực OTP của bạn");
            message.setText("Mã OTP của bạn là: " + otpCode + "\n\nMã này sẽ hết hạn trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.");
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Lỗi khi gửi email OTP đến " + toEmail + ": " + e.getMessage());
            throw new RuntimeException("Không thể gửi email xác thực. Vui lòng thử lại sau.");
        }
    }
}