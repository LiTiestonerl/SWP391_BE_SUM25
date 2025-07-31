package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.Enum.RatingType;
import com.example.smoking_cessation_platform.dto.rating.RatingRequest;
import com.example.smoking_cessation_platform.dto.rating.RatingResponse;
import com.example.smoking_cessation_platform.entity.Post;
import com.example.smoking_cessation_platform.entity.QuitPlan;
import com.example.smoking_cessation_platform.entity.Rating;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.mapper.RatingMapper;
import com.example.smoking_cessation_platform.repository.*;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
//
//    public RatingResponse createQuitPlanRating(RatingRequest request) {
//
//    }
//
//    public RatingResponse createCoachRating(RatingRequest request) {
//
//    }
//
//    public RatingResponse createPostRating(RatingRequest request) {
//
//    }

    /**
     * Lấy danh sách đánh giá cho một huấn luyện viên cụ thể.
     */
    public List<RatingResponse> getRatingsByCoach(Long coachId) {
        return ratingRepository.findByCoachUserId(coachId)
                .stream().map(ratingMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách đánh giá mà một thành viên đã thực hiện.
     */
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        return ratingRepository.findByMemberUserId(memberId)
                .stream().map(ratingMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Lấy tất cả đánh giá liên quan đến một kế hoạch cụ thể.
     */
    public List<RatingResponse> getRatingsByPlan(Integer planId) {
        return ratingRepository.findByQuitPlanPlanId(planId)
                .stream().map(ratingMapper::toResponse).collect(Collectors.toList());
    }
}
