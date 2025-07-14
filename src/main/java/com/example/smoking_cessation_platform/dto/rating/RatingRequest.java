package com.example.smoking_cessation_platform.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {
    private Integer ratingValue;
    private String feedbackText;
    private Long memberId;
    private Integer planId;
}
