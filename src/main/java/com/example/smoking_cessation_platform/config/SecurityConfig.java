package com.example.smoking_cessation_platform.config;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // api auth
                        .requestMatchers("/api/auth/register",
                                "/api/auth/email/resend-otp",
                                "/api/auth/email/verify",
                                "/api/auth/google"
                                "/api/auth/email/verify"
                        ).permitAll()
                        // api memberpackage
                        .requestMatchers(HttpMethod.GET, "/api/member-packages/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/member-packages").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/member-packages/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/member-packages/**").authenticated()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
