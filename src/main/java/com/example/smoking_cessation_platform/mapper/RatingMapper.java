package com.example.smoking_cessation_platform.mapper;

import com.example.smoking_cessation_platform.Enum.RatingType;
import com.example.smoking_cessation_platform.dto.rating.RatingResponse;
import com.example.smoking_cessation_platform.entity.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {
    public RatingResponse toResponse(Rating r) {
        if (r == null) return null;

        RatingResponse res = new RatingResponse();
        res.setRatingId(r.getRatingId());
        res.setRatingValue(r.getRatingValue());
        res.setRatingDate(r.getRatingDate());
        res.setFeedbackText(r.getFeedbackText());
        res.setStatus(r.getStatus());
        res.setRatingType(r.getRatingType());

        if (r.getMember() != null) {
            res.setMemberId(r.getMember().getUserId());
            res.setMemberName(r.getMember().getFullName());
        }

        // Rating cho Coach
        if (r.getRatingType() == RatingType.COACH && r.getCoach() != null) {
            res.setCoachId(r.getCoach().getUserId());
            res.setCoachName(r.getCoach().getFullName());
        }

        // Rating cho QuitPlan
        if (r.getRatingType() == RatingType.QUIT_PLAN && r.getQuitPlan() != null) {
            res.setPlanId(r.getQuitPlan().getPlanId());
            res.setPlanTitle(r.getQuitPlan().getTitle());
        }

        // Rating cho Post
        if (r.getRatingType() == RatingType.POST && r.getPost() != null) {
            res.setPostId(r.getPost().getPostId());
            res.setPostTitle(r.getPost().getTitle());
        }

        return res;
    }
}
