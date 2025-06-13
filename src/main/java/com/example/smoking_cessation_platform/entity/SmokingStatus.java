package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate; // Dùng LocalDate cho kiểu DATE

@Entity
@Getter
@Setter
@ToString
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

    @Column(name = "price_per_pack")
    private BigDecimal pricePerPack;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private CigarettePackage cigarettePackage;
}