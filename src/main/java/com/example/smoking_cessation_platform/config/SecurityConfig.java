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

                        // Member Package APIs (Permit all for testing, needs authentication/roles later)
                        .requestMatchers("/api/member-packages/**").permitAll()

                        // User Profile APIs (Permit all for testing, needs authentication/roles later)
                        .requestMatchers("/api/users/public/{userPublicId}").permitAll()
                        .requestMatchers("/api/users/{userId}").permitAll()

                        // Admin User Management APIs (Permit all for testing, needs ADMIN role later)
                        .requestMatchers("/api/admin/users/**").permitAll()

                        // Any other API requires authentication
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
