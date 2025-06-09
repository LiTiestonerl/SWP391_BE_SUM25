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
@Table(name = "quit_plan")
public class QuitPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "plan_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coach_id")
    private Long coachId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "recommended_package_id")
    private Long recommendedPackageId;

    @Column(name = "status")
    private String status = "active";

    @Column(name = "reason")
    private String reason;

    @Column(name = "stages_description")
    private String stagesDescription;

    @Column(name = "custom_notes")
    private String customNotes;

}
