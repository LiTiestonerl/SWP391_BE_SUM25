package com.example.smoking_cessation_platform.dto.rating;

import com.example.smoking_cessation_platform.Enum.RatingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {
    private Integer ratingValue;
    private String feedbackText;
    private RatingType ratingType; // "POST", "COACH", "PLAN" (tùy bạn muốn dùng không)
    private Integer postId;    // nếu rating bài viết
    private Long coachId;      // nếu rating coach
    private Integer planId;    // nếu rating kế hoạch
}
