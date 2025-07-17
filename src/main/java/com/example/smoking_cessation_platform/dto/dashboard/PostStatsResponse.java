package com.example.smoking_cessation_platform.dto.dashboard;

import com.example.smoking_cessation_platform.dto.dashboard.setup.TopPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostStatsResponse {
    private long totalPosts;             // Tổng số bài viết
    private long totalComments;          // Tổng số bình luận
    private double averageRating;        // Rating trung bình (nếu có)
    private List<TopPost> topPosts;      // Danh sách bài viết được comment nhiều nhất
}

