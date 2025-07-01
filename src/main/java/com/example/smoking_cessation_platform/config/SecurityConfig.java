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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                .authorizeHttpRequests(authorize -> authorize
                        // Public APIs
                        .requestMatchers("/api/auth/register",
                                "/api/auth/email/resend-otp",
                                "/api/auth/email/verify",
                                "/api/auth/google").permitAll()

                        // Member Package APIs (Temporarily permitAll, needs authentication/roles later)
                        .requestMatchers("/api/member-packages/**").permitAll()

                        // User Profile APIs (Temporarily permitAll, needs authentication/roles later)
                        .requestMatchers(HttpMethod.GET, "/api/users/public/{userPublicId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}").permitAll()

                        // Admin User Management APIs (Temporarily permitAll, needs ADMIN role later)
                        .requestMatchers("/api/admin/users/**").permitAll()

                        // Posts & Comments APIs (Temporarily permitAll, needs authentication/roles later)
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/**/comments").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**/comments/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**/comments/**").permitAll()

                        // Any other API requires authentication by default
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
