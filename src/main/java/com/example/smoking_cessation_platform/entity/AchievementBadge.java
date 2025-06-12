package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "userBadges") // Loại trừ để tránh lỗi stack overflow khi in toString
@SuperBuilder
@NoArgsConstructor
@Table(name = "achievement_badge")
public class AchievementBadge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "badge_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer badgeId;

    @Column(name = "badge_name")
    private String badgeName;

    @Column(name = "description")
    private String description;

    @Column(name = "criteria")
    private String criteria;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserBadge> userBadges = new HashSet<>();

    @OneToMany(mappedBy = "achievementBadge", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

}