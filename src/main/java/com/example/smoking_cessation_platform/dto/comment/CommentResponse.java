package com.example.smoking_cessation_platform.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Integer commentId;
    private String content;
    private LocalDateTime commentDate;
    private String status; // (active, deleted)
    private Integer postId;
    private Long userId;
    private String userName;
}
