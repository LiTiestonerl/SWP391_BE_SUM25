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
    private String ratingType; // "POST", "COACH", "PLAN" (tùy bạn muốn dùng không)
    private Integer postId;    // nếu rating bài viết
    private Long coachId;      // nếu rating coach
    private Integer planId;    // nếu rating kế hoạch
}
