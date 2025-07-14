package com.example.smoking_cessation_platform.security;

import com.example.smoking_cessation_platform.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("postSecurity")
public class PostSecurity {

    @Autowired
    private PostRepository postRepository;

    public boolean isOwner(Integer postId, Long userId) {
        return postRepository.findById(postId)
                .map(post -> post.getUser().getUserId().equals(userId))
                .orElse(false);
    }
}
