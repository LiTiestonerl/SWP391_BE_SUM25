package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "rating")
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rating_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "plan_id", nullable = false)
    private Integer planId;

    @Column(name = "rating_value")
    private Integer ratingValue;

    @Column(name = "rating_date")
    private LocalDateTime ratingDate;

    @Column(name = "status")
    private String status = "active";

    @Column(name = "feedback_text")
    private String feedbackText;

}
