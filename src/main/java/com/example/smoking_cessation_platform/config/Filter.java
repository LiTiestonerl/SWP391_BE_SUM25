package com.example.smoking_cessation_platform.config;

import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import com.example.smoking_cessation_platform.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final List<String> PUBLIC_APIS = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/auth/register",
            "/api/auth/email/resend-otp",
            "/api/auth/google",
            "/api/auth/email/verify",
            "/api/auth/login",
            "/api/payment/vnpay-return"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI(), request.getMethod());

        if (isPublicAPI) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing token");
            return;
        }

        User user;
        try {
            user = tokenService.getUserByToken(token);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
            return;
        } catch (MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        // Tạo CustomUserDetails từ User
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Tạo Authentication Token
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

        // Gán vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tiếp tục filter chain
        filterChain.doFilter(request, response);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // cắt đúng chuỗi "Bearer "
        }
        return null;
    }

    private boolean checkIsPublicAPI(String uri, String httpMethod) {
        // 1. OPTIONS (preflight)
        if (HttpMethod.OPTIONS.matches(httpMethod)) return true;

        // 2. GET bài viết + comment
        if (HttpMethod.GET.matches(httpMethod)) {
            if (PATH_MATCHER.match("/api/posts/**", uri) ||
                    PATH_MATCHER.match("/api/posts/**/comments/**", uri) ||
            PATH_MATCHER.match("/api/users/public/**",uri))
            {
                return true;
            }
        }

        // 3. Các API public khác
        if (PATH_MATCHER.match("/swagger-ui/**", uri) ||
                PATH_MATCHER.match("/v3/api-docs/**", uri) ||
                PATH_MATCHER.match("/swagger-ui.html", uri) ||
                PATH_MATCHER.match("/swagger-resources/**", uri) ||
                PATH_MATCHER.match("/webjars/**", uri) ||
                PATH_MATCHER.match("/api/auth/**", uri) ||
                PATH_MATCHER.match("/api/payment/vnpay-return", uri)) {
            return true;
        }

        return false;
    }
}
