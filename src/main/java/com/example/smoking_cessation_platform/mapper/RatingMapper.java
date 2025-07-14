package com.example.smoking_cessation_platform.mapper;

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

        res.setMemberId(r.getMember().getUserId());
        res.setMemberName(r.getMember().getFullName());

        if (r.getCoach() != null) {
            res.setCoachId(r.getCoach().getUserId());
            res.setCoachName(r.getCoach().getFullName());
        }

        res.setPlanId(r.getQuitPlan().getPlanId());
        res.setPlanTitle(r.getQuitPlan().getTitle());

        return res;
    }
}
