package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
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

}
