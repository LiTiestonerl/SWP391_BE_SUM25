package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString // Không cần exclude gì vì QuitProgress không chứa các Set khác
@SuperBuilder
@NoArgsConstructor
@Table(name = "quit_progress")
public class QuitProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "progress_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer progressId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "cigarettes_smoked")
    private Integer cigarettesSmoked;

    @Column(name = "money_spent")
    private BigDecimal moneySpent;

    @Column(name = "money_saved")
    private BigDecimal moneySaved;

    @Column(name = "smoking_free_days")
    private Integer smokingFreeDays;

    @Column(name = "health_status", columnDefinition = "TEXT")
    private String healthStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private QuitPlanStage quitPlanStage;
}