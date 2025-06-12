package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate; // Dùng LocalDate cho kiểu DATE

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "user_badge")
public class UserBadge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_badge_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userBadgeId;

    @Column(name = "date_achieved")
    private LocalDate dateAchieved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private AchievementBadge badge;
}