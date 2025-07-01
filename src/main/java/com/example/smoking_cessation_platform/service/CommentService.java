package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.Comment;
import com.example.smoking_cessation_platform.entity.Post;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.dto.comment.CommentRequest;
import com.example.smoking_cessation_platform.dto.comment.CommentResponse;
import com.example.smoking_cessation_platform.repository.CommentRepository;
import com.example.smoking_cessation_platform.repository.PostRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * Tạo một bình luận mới cho một bài viết.
     * API này yêu cầu người dùng đã được xác thực (đăng nhập).
     * @param postId ID của bài viết.
     * @param createDto DTO chứa thông tin bình luận cần tạo.
     * @param userId ID của người dùng tạo bình luận (lấy từ ngữ cảnh bảo mật).
     * @return DTO phản hồi của bình luận đã được tạo.
     */
    @Transactional
    public CommentResponse createComment(Integer postId, CommentRequest createDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại."));

        Comment comment = Comment.builder()
                .content(createDto.getContent())
                .commentDate(LocalDateTime.now())
                .status(createDto.getStatus() != null ? createDto.getStatus() : "active")
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    /**
     * Lấy tất cả bình luận cho một bài viết cụ thể.
     * @param postId ID của bài viết.
     * @return Danh sách DTO phản hồi của bình luận.
     */
    public List<CommentResponse> getCommentsByPostId(Integer postId) {
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Bài viết không tồn tại."));
        return commentRepository.findByPost_PostId(postId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả bình luận của một người dùng cụ thể.
     * @param userId ID của người dùng.
     * @return Danh sách DTO phản hồi của bình luận đó.
     */
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));
        return commentRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một bình luận theo ID.
     * @param commentId ID của bình luận.
     * @return Optional chứa DTO phản hồi bình luận (nếu tìm thấy).
     */
    public Optional<CommentResponse> getCommentById(Integer commentId) {
        return commentRepository.findById(commentId)
                .map(this::convertToDto);
    }

    /**
     * Cập nhật thông tin một bình luận.
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bình luận.
     * @param commentId ID của bình luận cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @param currentUserId ID của người dùng hiện tại (lấy từ ngữ cảnh bảo mật để kiểm tra quyền sở hữu).
     * @return Optional chứa DTO phản hồi bình luận đã cập nhật (nếu tìm thấy).
     */
    @Transactional
    public Optional<CommentResponse> updateComment(Integer commentId, CommentRequest updateDto, Long currentUserId) {
        return commentRepository.findById(commentId)
                .map(existingComment -> {
                    if (!existingComment.getUser().getUserId().equals(currentUserId)) {
                        throw new RuntimeException("Bạn không có quyền sửa bình luận này.");
                    }

                    existingComment.setContent(updateDto.getContent());
                    existingComment.setStatus(updateDto.getStatus() != null ? updateDto.getStatus() : existingComment.getStatus());

                    Comment updatedComment = commentRepository.save(existingComment);
                    return convertToDto(updatedComment);
                });
    }

    /**
     * Xóa một bình luận (thực hiện xóa mềm bằng cách thay đổi trạng thái).
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bình luận.
     * @param commentId ID của bình luận cần xóa.
     * @param currentUserId ID của người dùng hiện tại (lấy từ ngữ cảnh bảo mật để kiểm tra quyền sở hữu, hoặc Admin/Mod).
     * @return true nếu xóa thành công, false nếu không tìm thấy bình luận.
     */
    @Transactional
    public boolean deleteComment(Integer commentId, Long currentUserId) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    if (!comment.getUser().getUserId().equals(currentUserId)) {
                        throw new RuntimeException("Bạn không có quyền xóa bình luận này.");
                    }
                    comment.setStatus("deleted");
                    commentRepository.save(comment);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Phương thức hỗ trợ chuyển đổi từ Entity Comment sang CommentResponse.
     * @param comment Entity Comment.
     * @return CommentResponse.
     */
    private CommentResponse convertToDto(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .commentDate(comment.getCommentDate())
                .status(comment.getStatus())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .userName(comment.getUser().getUserName())
                .build();
    }
}
