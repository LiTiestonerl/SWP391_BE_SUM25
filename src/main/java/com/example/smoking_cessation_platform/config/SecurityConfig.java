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
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private Filter filter;

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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 fix lỗi preflig
                        .requestMatchers("/api/auth/email/resend-otp",
                                "/api/auth/email/verify",
                                "/api/auth/google",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/payment/vnpay-return").permitAll()

                        // APIs chỉ dành cho ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // APIs chỉ dành cho COACH
                        .requestMatchers("/api/coach/**").hasRole("COACH")

                        // APIs dành cho USER (bao gồm Coach và ADMIN nếu muốn)
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "COACH", "ADMIN")

                        // Chỉ ADMIN được phép tạo / chỉnh sửa / xóa member package
                        .requestMatchers(HttpMethod.POST, "/api/member-packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/member-packages/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/member-packages/*").hasRole("ADMIN")

                        // Cho phép tất cả user (authenticated) xem gói
                        .requestMatchers(HttpMethod.GET, "/api/member-packages/**").authenticated()

                        // APIs cần xác thực
                        .requestMatchers("/api/payment/vnpay-return").permitAll()
                        .requestMatchers("/api/payment/**").hasRole("USER")

                        // Public profile (ai cũng xem được)
                        .requestMatchers(HttpMethod.GET, "/api/users/public/**").permitAll()

                        // Hồ sơ cá nhân (cần đăng nhập)
                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}").hasAnyRole("USER", "COACH", "ADMIN")

                        // Cho phép đọc bài viết & comment (public)
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments/**").permitAll()

                        // Các thao tác khác yêu cầu login
                        .requestMatchers(HttpMethod.POST, "/api/posts").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("USER", "COACH", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/posts/**/comments").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**/comments/*").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**/comments/*").hasAnyRole("USER", "COACH", "ADMIN")

                        // Chỉ cho phép ROLE_USER thao tác với smoking-status
                        .requestMatchers(HttpMethod.POST, "/api/smoking-status").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/smoking-status").hasRole("USER")           // GET tất cả
                        .requestMatchers(HttpMethod.GET, "/api/smoking-status/**").hasRole("USER")         // GET theo ID
                        .requestMatchers(HttpMethod.PUT, "/api/smoking-status/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/smoking-status/**").hasRole("USER")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
