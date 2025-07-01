package com.example.smoking_cessation_platform.config;

import com.example.smoking_cessation_platform.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép các request swagger, auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 fix lỗi preflight
                        .requestMatchers("/api/auth/register",
                                "/api/auth/email/resend-otp",
                                "/api/auth/email/verify",
                                "/api/auth/google").permitAll()

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

                        // User Profile APIs (Temporarily permitAll, needs authentication/roles later)
                        .requestMatchers(HttpMethod.GET, "/api/users/public/{userPublicId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}").permitAll()

                        // Posts & Comments APIs (Temporarily permitAll, needs authentication/roles later)
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/**/comments").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**/comments/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**/comments/**").permitAll()

                        // Tất cả API khác bắt buộc phải đăng nhập
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
