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
@Table(name = "cigarette_recommendation")
public class CigaretteRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rec_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recId;


    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_package_id", nullable = false)
    private CigarettePackage fromPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_package_id", nullable = false)
    private CigarettePackage toPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smoking_status_id")
    private SmokingStatus smokingStatus;

}