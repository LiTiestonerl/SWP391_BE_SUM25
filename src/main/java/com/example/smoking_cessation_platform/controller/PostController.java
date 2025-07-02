package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.post.PostRequest;
import com.example.smoking_cessation_platform.dto.post.PostResponse;
import com.example.smoking_cessation_platform.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.smoking_cessation_platform.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@SecurityRequirement(name = "api")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * API để tạo một bài viết mới.
     * Yêu cầu: POST /api/posts
     * Body: PostCreateUpdateDTO (JSON)
     * API này yêu cầu người dùng đã được xác thực (đăng nhập).
     * @param createDto DTO chứa thông tin bài viết cần tạo.
     * @return ResponseEntity chứa DTO phản hồi của bài viết đã được tạo.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'COACH', 'ADMIN')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest createDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for createPost. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            PostResponse newPost = postService.createPost(createDto, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * API để lấy tất cả các bài viết công khai.
     * Yêu cầu: GET /api/posts
     * @return ResponseEntity chứa danh sách DTO phản hồi bài viết.
     */
    @GetMapping
    @Operation(summary = "Xem tất cả các bài post", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<List<PostResponse>> getAllPublishedPosts() {
        List<PostResponse> posts = postService.getAllPublishedPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * API để lấy một bài viết theo ID.
     * Yêu cầu: GET /api/posts/{postId}
     * @param postId ID của bài viết.
     * @return ResponseEntity chứa DTO phản hồi bài viết hoặc NOT_FOUND.
     */
    @GetMapping("/{postId}")
    @Operation(summary = "Xem post theo ID", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<PostResponse> getPostById(@PathVariable Integer postId) {
        Optional<PostResponse> post = postService.getPostById(postId);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để lấy tất cả bài viết của một người dùng cụ thể.
     * Yêu cầu: GET /api/posts/user/{userId}
     * @param userId ID của người dùng.
     * @return ResponseEntity chứa danh sách DTO phản hồi bài viết của người dùng đó.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Xem post theo ID User", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<List<PostResponse>> getPostsByUserId(@PathVariable Long userId) {
        List<PostResponse> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * API để cập nhật thông tin một bài viết.
     * Yêu cầu: PUT /api/posts/{postId}
     * Body: PostCreateUpdateDTO (JSON)
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bài viết hoặc Admin.
     * @param postId ID của bài viết cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @return ResponseEntity chứa DTO phản hồi bài viết đã cập nhật hoặc thông báo lỗi.
     */
    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN') or ((hasRole('COACH') or hasRole('USER')) and @postSecurity.isOwner(#postId, principal.userId))")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Integer postId, @Valid @RequestBody PostRequest updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for updatePost. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            Optional<PostResponse> updatedPost = postService.updatePost(postId, updateDto, currentUserId);
            return updatedPost.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403
        }
    }

    /**
     * API để xóa một bài viết (thực hiện xóa mềm bằng cách thay đổi trạng thái).
     * Yêu cầu: DELETE /api/posts/{postId}
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bài viết hoặc Admin.
     * @param postId ID của bài viết cần xóa.
     * @return ResponseEntity chứa NO_CONTENT nếu xóa thành công, hoặc NOT_FOUND.
     */
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN') or ((hasRole('COACH') or hasRole('USER')) and @postSecurity.isOwner(#postId, principal.userId))")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for deletePost. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            boolean deleted = postService.deletePost(postId, currentUserId);
            if (deleted) {
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 for bidden
        }
    }
}
