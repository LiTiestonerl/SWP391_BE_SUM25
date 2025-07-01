package com.example.smoking_cessation_platform.security;

import com.example.smoking_cessation_platform.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
public class CommentSecurity {

    @Autowired
    private CommentRepository commentRepository;

    // Check xem người dùng hiện tại có phải chủ bình luận không
    public boolean isOwner(Integer commentId, Long userId) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getUser().getUserId().equals(userId))
                .orElse(false);
    }

    // Check xem comment đó có thuộc về một USER hay không (để Coach có quyền xóa)
    public boolean isCommentOwnedByUser(Integer commentId) {
        return commentRepository.findById(commentId)
                .map(comment -> "USER".equalsIgnoreCase(comment.getUser().getRole().getRoleName()))
                .orElse(false);
    }
}

