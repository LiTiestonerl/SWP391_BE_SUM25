package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate; // Dùng LocalDate cho kiểu DATE
import java.util.HashSet; // Cần import Set và HashSet
import java.util.Set;

@Entity
@Getter
@Setter
// Loại trừ các collection để tránh lỗi StackOverflowError khi gọi toString()
@ToString(exclude = {"quitPlanStages", "notifications", "ratings"})
@SuperBuilder
@NoArgsConstructor
@Table(name = "quit_plan")
public class QuitPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "plan_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "status")
    private String status = "active";

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "stages_description", columnDefinition = "TEXT")
    private String stagesDescription;

    @Column(name = "custom_notes", columnDefinition = "TEXT")
    private String customNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Users coach;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_package_id")
    private CigarettePackage recommendedPackage;

    @OneToMany(mappedBy = "quitPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<QuitPlanStage> quitPlanStages = new HashSet<>();

    @OneToMany(mappedBy = "quitPlan", fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "quitPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Rating> ratings = new HashSet<>();

}