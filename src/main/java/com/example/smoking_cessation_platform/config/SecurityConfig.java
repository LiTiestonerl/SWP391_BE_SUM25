package com.example.smoking_cessation_platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    Filter filter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Public APIs
                        .requestMatchers("/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/auth/register",
                                "/api/auth/email/resend-otp",
                                "/api/auth/google",
                                "/api/auth/email/verify",
                                "/api/auth/login").permitAll()

                        // ✅ APIs chỉ dành cho ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ APIs chỉ dành cho DOCTOR
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")

                        // ✅ APIs dành cho USER (bao gồm DOCTOR và ADMIN nếu muốn)
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "DOCTOR", "ADMIN")

                        // ✅ APIs dành cho tất cả người dùng đã đăng nhập
                        .requestMatchers("/api/member-packages/**").authenticated()

                        // ✅ Tất cả API khác bắt buộc phải đăng nhập
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
