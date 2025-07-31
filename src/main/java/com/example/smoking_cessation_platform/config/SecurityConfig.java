package com.example.smoking_cessation_platform.config;

import com.example.smoking_cessation_platform.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép các request swagger, auth

                        // 1️ Swagger + Auth (public)
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/email/resend-otp",
                                "/api/auth/email/verify",
                                "/api/auth/google",
                                "/api/payment/vnpay-return",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",// giữ nguyên permitAll
                                "/api/auth/refresh"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 fix lỗi preflig

                        // 3️ ADMIN‑only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // APIs dành cho USER (bao gồm Coach và ADMIN nếu muốn)
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "COACH", "ADMIN")

                        // 6️ Member‑package
                        .requestMatchers(HttpMethod.POST,   "/api/member-packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/member-packages/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/member-packages/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/member-packages/**").authenticated()

                        // 7️ Payment (đã có vnpay-return permitAll ở trên)
                        .requestMatchers("/api/payment/**").authenticated()

                        // 8️ Public user profile
                        .requestMatchers(HttpMethod.GET, "/api/users/public/**").permitAll()

                        // 9️ Hồ sơ cá nhân (GET 1 user – dùng * thay {userId})
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAnyRole("USER", "COACH", "ADMIN")

                        //  Bài viết & comment (public đọc, authenticated CRUD)
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments/**").permitAll()

                        .requestMatchers(HttpMethod.POST,   "/api/posts").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/posts/**").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("USER", "COACH", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/posts/{postId}/comments").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/{postId}/comments/**").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/{postId}/comments/**").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/posts/*/comments").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/posts/*/comments/*").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/*/comments/*").hasAnyRole("USER", "COACH", "ADMIN")


                        // Gói thuốc (public GET, hạn chế POST/PUT/DELETE)
                        .requestMatchers(HttpMethod.POST, "/api/cigarette-packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cigarette-packages/{cigaretteId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cigarette-packages/{cigaretteId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cigarette-packages/**").hasAnyRole("ADMIN", "USER")

                        // CigaretteRecommendation
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/{recId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/for-cigarette/{cigaretteId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/lighter-nicotine/{cigaretteId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/same-flavor/{cigaretteId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/same-brand-lighter/{cigaretteId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/best/{cigaretteId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/by-smoking-status/{smokingStatusId}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/api/cigarette-recommendations/all").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PATCH,"/api/cigarette-recommendations/admin/{recId}/toggle-active").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/cigarette-recommendations/admin/{recId}/priority").hasRole("ADMIN")

                        //AchievementBadge
                        .requestMatchers(HttpMethod.POST, "/api/achievement-badge").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/achievement-badge").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/achievement-badge/{id}").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/achievement-badge/name/{name}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/achievement-badge/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/achievement-badge/{id}").hasRole("ADMIN")

                        //UserBadge
                        .requestMatchers("/api/user_badge/**").hasRole("USER")

                        //Notification
                        .requestMatchers(HttpMethod.POST, "/api/notifications").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notifications/me").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/notifications/{id}/read").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/notifications/{id}").hasRole("USER")

                        //Rating
                        .requestMatchers(HttpMethod.POST,"/api/rating").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/rating/coach/{coachId}").hasRole("COACH")
                        .requestMatchers(HttpMethod.GET, "/api/rating/member/{memberId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/rating/plan/{planId}").hasRole("ADMIN")

                        //QuitPlan
                        .requestMatchers(HttpMethod.GET,"/api/quit-plan/{planId}").hasAnyRole("USER","COACH","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/quit-plan/user/{userId}").hasAnyRole("USER","COACH")
                        .requestMatchers(HttpMethod.GET, "/api/quit-plan/free").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/quit-plan/user/{userId}/current").hasRole("USER")

                        .requestMatchers(HttpMethod.POST, "/api/quit-plan").hasAnyRole("USER","ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/coach").hasRole("COACH")
                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/user").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/cancel").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/quit-plan/{planId}/complete").authenticated()

                        .requestMatchers(HttpMethod.DELETE, "/api/quit-plan/{planId}/user/{userId}").hasAnyRole("USER","ADMIN")

                        //Quit Progress
                        .requestMatchers(HttpMethod.PUT,"/api/quit-progress/update").hasAnyRole("USER","ADMIN")

                        // Smoking Status endpoints
                        .requestMatchers(HttpMethod.POST, "/api/smoking-status").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/smoking-status").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/smoking-status").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/smoking-status").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/smoking-status/{userId}").hasRole("ADMIN")

                        //Chat
                        .requestMatchers(HttpMethod.GET, "/api/chat/sessions").hasAnyRole("USER", "COACH")
                        .requestMatchers(HttpMethod.GET, "/api/chat/sessions/{id}").hasAnyRole("USER", "COACH")
                        .requestMatchers(HttpMethod.GET, "/api/chat/sessions/{id}/messages").hasAnyRole("USER", "COACH")
                        .requestMatchers(HttpMethod.POST, "/api/chat/sessions").hasRole("USER")

                        //AdminStatus
                        .requestMatchers("/api/admin/dashboard/**").hasRole("ADMIN")

                        //User select coach
                        .requestMatchers(HttpMethod.GET,"/api/coach").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/coach/{id}").hasRole("USER")

                        //User profile
                        .requestMatchers(HttpMethod.PUT, "/api/user/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/user/profile/{userId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/user/profile/public/{userPublicId}").authenticated()

                        //User member package
                        .requestMatchers(HttpMethod.GET,"/api/user-membership/me").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/user-membership/me").hasRole("USER")

                        // 13️ Mặc định: phải login
                        .requestMatchers(HttpMethod.GET, "/api/coach/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
