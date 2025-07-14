package com.example.smoking_cessation_platform.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private Integer ratingId;
    private Integer ratingValue;
    private LocalDateTime ratingDate;
    private String feedbackText;
    private String status;

    private Long memberId;
    private String memberName;

    private Long coachId;
    private String coachName;

    private Integer planId;
    private String planTitle;
}
