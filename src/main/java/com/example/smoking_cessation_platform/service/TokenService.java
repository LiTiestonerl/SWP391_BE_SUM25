package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.exception.UserNotFoundException;
import com.example.smoking_cessation_platform.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {
    @Autowired
    private UserRepository userRepository;

    private final String SECRET_KEY = "HT4bb6d1dfbafb64a681139d1586b6f1160d18159afd578c8c79136d7490630407c";

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔹 Tạo Access Token (thời hạn ngắn)
    public String generateAccessToken(User user) {
        List<String> roles = List.of("ROLE_" + user.getRole().getRoleName());

        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 phút
                .claim("roles", roles)
                .signWith(getSigninKey())
                .compact();
    }

    // 🔹 Tạo Refresh Token (thời hạn dài hơn, ví dụ 7 ngày)
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 ngày
                .signWith(getSigninKey())
                .compact();
    }

    // 🔹 Xác minh token (có thể dùng cho cả access & refresh)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 🔹 Lấy User từ token (có thể dùng với refresh hoặc access token)
    public User getUserByToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long id = Long.parseLong(claims.getSubject());
        return userRepository.findByUserId(id)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng với ID: " + id));
    }
}
