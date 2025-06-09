package com.example.smoking_cessation_platform.Repository;

import com.example.smoking_cessation_platform.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {

}