package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.comment.CommentRequest;
import com.example.smoking_cessation_platform.dto.comment.CommentResponse;
import com.example.smoking_cessation_platform.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
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
@RequestMapping("/api/posts/{postId}/comments")
@SecurityRequirement(name = "api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * API để tạo một bình luận mới cho một bài viết.
     * Yêu cầu: POST /api/posts/{postId}/comments
     * Body: CommentRequest (JSON)
     * API này yêu cầu người dùng đã được xác thực.
     * @param postId ID của bài viết mà bình luận thuộc về.
     * @param createDto DTO chứa thông tin bình luận cần tạo.
     * @return ResponseEntity chứa DTO phản hồi của bình luận đã được tạo.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','COACH')")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Integer postId, @Valid @RequestBody CommentRequest createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for createComment. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            CommentResponse newComment = commentService.createComment(postId, createDto, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * API để lấy tất cả bình luận cho một bài viết cụ thể.
     * Yêu cầu: GET /api/posts/{postId}/comments
     * @param postId ID của bài viết.
     * @return ResponseEntity chứa danh sách DTO phản hồi của bình luận.
     */
    @GetMapping
    @PermitAll
    @Operation(summary = "Xem tất cả bình luận", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Integer postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * API để lấy một bình luận theo ID.
     * Yêu cầu: GET /api/posts/{postId}/comments/{commentId}
     * @param commentId ID của bình luận.
     * @return ResponseEntity chứa DTO phản hồi bình luận hoặc NOT_FOUND.
     */
    @GetMapping("/{commentId}")
    @PermitAll
    @Operation(summary = "Xem 1 bình luận", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Integer commentId) {
        Optional<CommentResponse> comment = commentService.getCommentById(commentId);
        return comment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để cập nhật thông tin một bình luận.
     * Yêu cầu: PUT /api/posts/{postId}/comments/{commentId}
     * Body: CommentRequest (JSON)
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bình luận.
     * @param commentId ID của bình luận cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @param authentication Đối tượng Authentication từ Spring Security.
     * @return ResponseEntity chứa DTO phản hồi bình luận đã cập nhật hoặc thông báo lỗi.
     */
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER','COACH') and @commentSecurity.isOwner(#commentId, principal.userId)")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Integer commentId, @Valid @RequestBody CommentRequest updateDto, Authentication authentication) {
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for updateComment. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            Optional<CommentResponse> updatedComment = commentService.updateComment(commentId, updateDto, currentUserId);
            return updatedComment.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden
        }
    }

    /**
     * API để xóa một bình luận.
     * Yêu cầu: DELETE /api/posts/{postId}/comments/{commentId}
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bình luận.
     * @param commentId ID của bình luận cần xóa.
     * @param authentication Đối tượng Authentication từ Spring Security.
     * @return ResponseEntity chứa NO_CONTENT nếu xóa thành công, hoặc NOT_FOUND.
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("(" +
            "hasRole('USER')  and @commentSecurity.isOwner(#commentId, principal.userId)" +
            ") or (" +
            "hasRole('COACH') and @commentSecurity.isCommentOwnedByUser(#commentId)" +
            ")")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId, Authentication authentication) {
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for deleteComment. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            boolean deleted = commentService.deleteComment(commentId, currentUserId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
    }
}
