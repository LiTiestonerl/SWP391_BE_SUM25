package com.example.smoking_cessation_platform.entity;

import com.example.smoking_cessation_platform.enums.NicotineStrength;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"smokingStatuses", "quitPlans", "cigaretteRecommendationsFrom", "cigaretteRecommendationsTo"})
@SuperBuilder
@NoArgsConstructor
@Table(name = "cigarette_package")
public class CigarettePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cigarette_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cigaretteId;

    @Column(name = "cigarette_name", nullable = false)
    private String cigaretteName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "nicotene_strength", nullable = false)
    private NicotineStrength nicoteneStrength;

    @Column(name = "flavor", nullable = false)
    private String flavor;

    @Column(name = "sticks_per_pack", nullable = false)
    private Integer sticksPerPack;

    @OneToMany(mappedBy = "cigarettePackage", fetch = FetchType.LAZY)
    private Set<SmokingStatus> smokingStatuses = new HashSet<>();

    @OneToMany(mappedBy = "recommendedPackage", fetch = FetchType.LAZY)
    private Set<QuitPlan> quitPlans = new HashSet<>();

    @OneToMany(mappedBy = "fromPackage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CigaretteRecommendation> cigaretteRecommendationsFrom = new HashSet<>();

    @OneToMany(mappedBy = "toPackage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CigaretteRecommendation> cigaretteRecommendationsTo = new HashSet<>();

}