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

@Service
public class TokenService {
    @Autowired
    UserRepository userRepository;
    private final String SECRET_KEY = "HT4bb6d1dfbafb64a681139d1586b6f1160d18159afd578c8c79136d7490630407c";

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //tao token
    public String generateToken(User user) {
        String token = Jwts.builder()
                .subject(user.getUserId()+"")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getSigninKey())
                .compact();
        return token;
    }
    //verify token
    public User getUserByToken (String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String idString = claims.getSubject();
        long id = Long.parseLong(idString);

        User user = userRepository.findByUserId(id).orElseThrow(()->new UserNotFoundException("Không tìm thấy người dùng với ID: " + id));
        return user;
    }
}
