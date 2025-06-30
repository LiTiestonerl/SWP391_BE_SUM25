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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép các request swagger, auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 fix lỗi preflight
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

                        // APIs chỉ dành cho ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // APIs chỉ dành cho COACH
                        .requestMatchers("/api/doctor/**").hasRole("COACH")

                        // APIs dành cho USER (bao gồm Coach và ADMIN nếu muốn)
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "COACH", "ADMIN")

                        // APIs cần xác thực
                        .requestMatchers("/api/member-packages/**").authenticated()
                        .requestMatchers("/api/payment/vnpay-return").permitAll()
                        .requestMatchers("/api/payment/**").authenticated()

                        // Tất cả API khác bắt buộc phải đăng nhập
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
