package com.example.smoking_cessation_platform.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "quit_plan_stage")
public class QuitPlanStage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "stage_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stageId;

    @Column(name = "plan_id", nullable = false)
    private Integer planId;

    @Column(name = "stage_name")
    private String stageName;

    @Column(name = "stage_start_date")
    private LocalDate stageStartDate;

    @Column(name = "stage_end_date")
    private LocalDate stageEndDate;

    @Column(name = "target_cigarettes_per_day")
    private Integer targetCigarettesPerDay;

    @Column(name = "notes")
    private String notes;

}
