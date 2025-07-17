package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUser_UserId(Long userId);
    List<Post> findByStatus(String status);
    List<Post> findByStatusNot(String status);


    @Query("SELECT p.id, p.title, COUNT(c.id) as commentCount " +
            "FROM Post p LEFT JOIN Comment c ON c.post.id = p.id " +
            "GROUP BY p.id, p.title " +
            "ORDER BY commentCount DESC")
    List<Object[]> findTopPosts();
}
