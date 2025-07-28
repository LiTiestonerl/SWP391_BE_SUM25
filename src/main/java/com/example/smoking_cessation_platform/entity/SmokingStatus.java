package com.example.smoking_cessation_platform.entity;

import com.example.smoking_cessation_platform.Enum.NicotineStrength;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"recommendations", "user", "cigarettePackage"})
@SuperBuilder
@NoArgsConstructor
@Table(name = "smoking_status")
public class SmokingStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "status_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;

    @Column(name = "cigarettes_per_day")
    private Integer cigarettesPerDay;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "preferred_flavor")
    private String preferredFlavor;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_nicotine_level")
    private NicotineStrength preferredNicotineLevel; // LOW, MEDIUM, HIGH, ZERO

    @Column(name = "record_date")
    private LocalDate recordDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private CigarettePackage cigarettePackage;

    @OneToMany(mappedBy = "smokingStatus", fetch = FetchType.LAZY)
    private Set<CigaretteRecommendation> recommendations = new HashSet<>();
}