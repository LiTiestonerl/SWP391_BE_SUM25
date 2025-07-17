package com.example.smoking_cessation_platform.dto.dashboard.setup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopPost {
    private Integer postId;
    private String title;
    private long commentCount;
}