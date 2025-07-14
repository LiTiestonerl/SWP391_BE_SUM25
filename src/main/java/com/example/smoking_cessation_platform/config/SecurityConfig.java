package com.example.smoking_cessation_platform.config;

import com.example.smoking_cessation_platform.security.CustomUserDetailsService;
import io.swagger.v3.oas.models.PathItem;
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
import org.springframework.security.config.http.SessionCreationPolicy;
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
                                "/api/payment/vnpay-return"              // giữ nguyên permitAll
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 fix lỗi preflig

                        // 3️ ADMIN‑only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 4️ COACH‑only
                        .requestMatchers("/api/coach/**").hasRole("COACH")

                        // 5️ USER + COACH + ADMIN
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
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/comments/**").permitAll()

                        .requestMatchers(HttpMethod.POST,   "/api/posts").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/posts/**").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("USER", "COACH", "ADMIN")

                        .requestMatchers(HttpMethod.POST,   "/api/posts/*/comments").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/posts/*/comments/*").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/*/comments/*").hasAnyRole("USER", "COACH", "ADMIN")

                        // 11️ Smoking‑status (ROLE_USER)
                        .requestMatchers("/api/smoking-status/**").hasRole("USER")


                        // Gói thuốc (public GET, hạn chế POST/PUT/DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/cigarette-packages/*/recommendations").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cigarette-packages/**").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cigarette-packages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cigarette-packages/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cigarette-packages/*").hasRole("ADMIN")

                        // Achievement Badge (public GET, hạn chế POST/PUT/DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/achievement_badge").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/achievement_badge/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/achievement_badge/name/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/achievement_badge").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/achievement_badge/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/achievement_badge/*").hasRole("ADMIN")

                        // User Badge (Quản lý huy hiệu đã đạt)
                        .requestMatchers("/api/user-badges/**").authenticated()

                        // Notification
                        .requestMatchers("/api/notifications/**").authenticated()

                        // Quit Plan
                        .requestMatchers(HttpMethod.POST, "/api/quit-plan").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/quit-plan/{planId}").hasAnyRole("USER", "COACH", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/quit-plan/user/{userId}").hasAnyRole("USER", "COACH", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/coach").hasAnyRole("COACH", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/user").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/quit-plan/{planId}/user/{userId}").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/quit-plan/free").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/quit-plan/user/{userId}/current").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/cancel").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/quit-plan/{planId}/complete").hasAnyRole("USER", "ADMIN")
                        //rating
                        .requestMatchers(HttpMethod.POST, "/api/ratings").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/ratings/coach/*").hasAnyRole( "COACH")
                        .requestMatchers(HttpMethod.GET, "/api/ratings/member/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/ratings/plan/*").hasRole("ADMIN")
                        // 13️ Mặc định: phải login
                        .anyRequest().authenticated()
                )

                // Stateless nếu bạn dùng JWT
                .sessionManagement(cfg -> cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
