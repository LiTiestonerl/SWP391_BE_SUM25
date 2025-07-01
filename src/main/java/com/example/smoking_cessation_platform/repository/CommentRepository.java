package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost_PostId(Integer postId);
    List<Comment> findByUser_UserId(Long userId);
}
