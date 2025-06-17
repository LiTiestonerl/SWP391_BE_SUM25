package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.EmailVerificationToken;
import com.example.smoking_cessation_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByUserAndOtpCodeAndExpiresAtAfterAndConfirmedAtIsNull(
            User user, String otpCode, LocalDateTime now);

    Optional<EmailVerificationToken> findFirstByUserAndConfirmedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
            User user, LocalDateTime now);
}