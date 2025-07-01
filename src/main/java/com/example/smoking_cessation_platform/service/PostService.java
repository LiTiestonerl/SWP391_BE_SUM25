package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.Post;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.dto.post.PostRequest;
import com.example.smoking_cessation_platform.dto.post.PostResponse;
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
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tạo một bài viết mới.
     * @param createDto DTO chứa thông tin bài viết cần tạo.
     * @param userId ID của người dùng tạo bài viết (lấy từ ngữ cảnh bảo mật).
     * @return DTO phản hồi của bài viết đã được tạo.
     */
    @Transactional
    public PostResponse createPost(PostRequest createDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        Post post = Post.builder()
                .title(createDto.getTitle())
                .content(createDto.getContent())
                .postDate(LocalDateTime.now())
                .status(createDto.getStatus() != null ? createDto.getStatus() : "published")
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    /**
     * Lấy tất cả các bài viết
     * @return Danh sách các DTO phản hồi bài viết.
     */
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả các bài viết published
     * @return Danh sách các DTO phản hồi bài viết.
     */
    public List<PostResponse> getAllPublishedPosts() {
        return postRepository.findByStatus("published").stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một bài viết theo ID.
     * @param postId ID của bài viết.
     * @return Optional chứa DTO phản hồi bài viết (nếu tìm thấy).
     */
    public Optional<PostResponse> getPostById(Integer postId) {
        return postRepository.findById(postId)
                .map(this::convertToDto);
    }

    /**
     * Lấy tất cả bài viết của một người dùng cụ thể.
     * @param userId ID của người dùng.
     * @return Danh sách DTO phản hồi bài viết của người dùng đó.
     */
    public List<PostResponse> getPostsByUserId(Long userId) {
        return postRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin một bài viết.
     * @param postId ID của bài viết cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @param currentUserId ID của người dùng hiện tại (lấy từ ngữ cảnh bảo mật để kiểm tra quyền sở hữu).
     * @return Optional chứa DTO phản hồi bài viết đã cập nhật (nếu tìm thấy).
     */
    @Transactional
    public Optional<PostResponse> updatePost(Integer postId, PostRequest updateDto, Long currentUserId) {
        return postRepository.findById(postId)
                .map(existingPost -> {
                    // nhớ thêm phân quyền chỗ này giúp anh
                    if (!existingPost.getUser().getUserId().equals(currentUserId)) {
                        throw new RuntimeException("Bạn không có quyền sửa bài viết này.");
                    }

                    existingPost.setTitle(updateDto.getTitle());
                    existingPost.setContent(updateDto.getContent());
                    existingPost.setStatus(updateDto.getStatus() != null ? updateDto.getStatus() : existingPost.getStatus());

                    Post updatedPost = postRepository.save(existingPost);
                    return convertToDto(updatedPost);
                });
    }

    /**
     * Xóa một bài viết
     * @param postId ID của bài viết cần xóa.
     * @param currentUserId ID của người dùng hiện tại
     * @return true nếu xóa thành công, false nếu không tìm thấy bài viết.
     */
    @Transactional
    public boolean deletePost(Integer postId, Long currentUserId) {
        return postRepository.findById(postId)
                .map(post -> {
                    // thêm phân quyền admin hoặc user sở hữu bài viết mới được xoá nha em
                    if (!post.getUser().getUserId().equals(currentUserId)) {
                        throw new RuntimeException("Bạn không có quyền xóa bài viết này.");
                    }
                    post.setStatus("deleted");
                    postRepository.save(post);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Phương thức hỗ trợ chuyển đổi từ Entity Post sang PostResponseDTO.
     * @param post Entity Post.
     * @return PostResponseDTO.
     */
    private PostResponse convertToDto(Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .postDate(post.getPostDate())
                .status(post.getStatus())
                .userId(post.getUser().getUserId())
                .userName(post.getUser().getUserName())
                .build();
    }
}
