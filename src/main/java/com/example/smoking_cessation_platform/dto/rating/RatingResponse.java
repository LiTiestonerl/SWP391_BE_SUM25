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
    private String feedbackText;
    private String ratingType;
    private LocalDateTime ratingDate;
    private String status;

    private Long memberId;
    private String memberName;

    // Thêm các ID tùy loại
    private Integer postId;
    private String postTitle;

    private Long coachId;
    private String coachName;

    private Integer planId;
    private String planTitle;
}
