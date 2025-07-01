package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUser_UserId(Long userId);
    List<Post> findByStatus(String status);
    List<Post> findByStatusNot(String status);
}
