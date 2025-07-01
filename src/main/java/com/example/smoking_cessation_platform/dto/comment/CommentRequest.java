package com.example.smoking_cessation_platform.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 65535, message = "Nội dung bình luận quá dài")
    private String content;

    // status (active, deleted)
    // default active
    @Size(max = 20, message = "Trạng thái không hợp lệ")
    private String status;
}
