package com.example.smoking_cessation_platform.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Integer postId;
    private String title;
    private String content;
    private LocalDateTime postDate;
    private String status;     // status (published, deleted)
    private Long userId;
    private String userName;
}
