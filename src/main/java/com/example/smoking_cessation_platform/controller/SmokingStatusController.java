package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.smokingstatus.SmokingStatusRequest;
import com.example.smoking_cessation_platform.dto.smokingstatus.SmokingStatusResponse;
import com.example.smoking_cessation_platform.service.SmokingStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.smoking_cessation_platform.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/smoking-status")
public class SmokingStatusController {

    @Autowired
    private SmokingStatusService smokingStatusService;

    /**
     * API để tạo một bản ghi thói quen hút thuốc mới.
     * Yêu cầu: POST /api/smoking-status
     * Body: SmokingStatusCreateUpdateDTO (JSON)
     * API này yêu cầu người dùng đã được xác thực (đăng nhập).
     * @param createDto DTO chứa thông tin bản ghi cần tạo.
     * @param authentication Đối tượng Authentication từ Spring Security.
     * @return ResponseEntity chứa DTO phản hồi của bản ghi đã được tạo.
     */
    @PostMapping
    public ResponseEntity<SmokingStatusResponse> createSmokingStatus(@Valid @RequestBody SmokingStatusRequest createDto, Authentication authentication) {
        // nhớ phân quyền
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for createSmokingStatus. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            SmokingStatusResponse newStatus = smokingStatusService.createSmokingStatus(createDto, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newStatus);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * API để lấy tất cả các bản ghi thói quen hút thuốc của người dùng đang đăng nhập.
     * Yêu cầu: GET /api/smoking-status
     * API này yêu cầu người dùng đã được xác thực (đăng nhập).
     * @param authentication Đối tượng Authentication từ Spring Security.
     * @return ResponseEntity chứa danh sách DTO phản hồi bản ghi thói quen hút thuốc.
     */
    @GetMapping
    public ResponseEntity<List<SmokingStatusResponse>> getAllMySmokingStatuses(Authentication authentication) {
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for getAllMySmokingStatuses. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        List<SmokingStatusResponse> statuses = smokingStatusService.getAllSmokingStatusesByUserId(currentUserId);
        return ResponseEntity.ok(statuses);
    }

    /**
     * API để lấy một bản ghi thói quen hút thuốc theo ID.
     * Yêu cầu: GET /api/smoking-status/{statusId}
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bản ghi.
     * @param statusId ID của bản ghi.
     * @return ResponseEntity chứa DTO phản hồi bản ghi hoặc NOT_FOUND.
     */
    @GetMapping("/{statusId}")
    public ResponseEntity<SmokingStatusResponse> getSmokingStatusById(@PathVariable Integer statusId) {
        // phân quyền
        Optional<SmokingStatusResponse> status = smokingStatusService.getSmokingStatusById(statusId);
        return status.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để cập nhật thông tin một bản ghi thói quen hút thuốc.
     * Yêu cầu: PUT /api/smoking-status/{statusId}
     * Body: SmokingStatusCreateUpdateDTO (JSON)
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bản ghi.
     * @param statusId ID của bản ghi cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @param authentication Đối tượng Authentication từ Spring Security.
     * @return ResponseEntity chứa DTO phản hồi bản ghi đã cập nhật hoặc thông báo lỗi.
     */
    @PutMapping("/{statusId}")
    public ResponseEntity<SmokingStatusResponse> updateSmokingStatus(@PathVariable Integer statusId, @Valid @RequestBody SmokingStatusRequest updateDto, Authentication authentication) {
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for updateSmokingStatus. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            Optional<SmokingStatusResponse> updatedStatus = smokingStatusService.updateSmokingStatus(statusId, updateDto, currentUserId);
            return updatedStatus.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Trả về 403 Forbidden nếu không có quyền
        }
    }

    /**
     * API để xóa một bản ghi thói quen hút thuốc.
     * Yêu cầu: DELETE /api/smoking-status/{statusId}
     * API này yêu cầu người dùng đã được xác thực và là chủ sở hữu bản ghi.
     * @param statusId ID của bản ghi cần xóa.
     * @param authentication Đối tượng Authentication từ Spring Security.
     * @return ResponseEntity chứa NO_CONTENT nếu xóa thành công, hoặc NOT_FOUND.
     */
    @DeleteMapping("/{statusId}")
    public ResponseEntity<Void> deleteSmokingStatus(@PathVariable Integer statusId, Authentication authentication) {
        Long currentUserId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails for deleteSmokingStatus. Current type: " + authentication.getPrincipal().getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            boolean deleted = smokingStatusService.deleteSmokingStatus(statusId, currentUserId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Trả về 403 Forbidden nếu không có quyền
        }
    }
}
