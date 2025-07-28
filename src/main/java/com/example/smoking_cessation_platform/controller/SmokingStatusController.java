package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.smokingStatus.SmokingStatusRequest;
import com.example.smoking_cessation_platform.dto.smokingStatus.SmokingStatusResponse;
import com.example.smoking_cessation_platform.service.SmokingStatusService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.smoking_cessation_platform.security.CustomUserDetails;

import java.util.Optional;

@RestController
@RequestMapping("/api/smoking-status")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('USER')")
public class SmokingStatusController {

    @Autowired
    private SmokingStatusService smokingStatusService;

    /**
     * API để tạo một bản ghi thói quen hút thuốc mới.
     */
    @PostMapping
    public ResponseEntity<SmokingStatusResponse> createSmokingStatus(@Valid @RequestBody SmokingStatusRequest createDto, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
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
     * API để cập nhật thông tin bản ghi thói quen hút thuốc của user hiện tại.
     */
    @PutMapping
    public ResponseEntity<SmokingStatusResponse> updateSmokingStatus(@Valid @RequestBody SmokingStatusRequest updateDto, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            Optional<SmokingStatusResponse> updatedStatus = smokingStatusService.updateSmokingStatusByUserId(currentUserId, updateDto);
            return updatedStatus.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * API để xóa bản ghi thói quen hút thuốc của user hiện tại.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteSmokingStatus(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            boolean deleted = smokingStatusService.deleteSmokingStatusByUserId(currentUserId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * API để lấy thông tin smoking status của user hiện tại.
     */
    @GetMapping
    public ResponseEntity<SmokingStatusResponse> getMySmokingStatus(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        Optional<SmokingStatusResponse> status = smokingStatusService.getSmokingStatusByUserId(currentUserId);
        return status.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * API để admin lấy smoking status của user khác (nếu cần).
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SmokingStatusResponse> getSmokingStatusByUserId(@PathVariable Long userId) {
        Optional<SmokingStatusResponse> status = smokingStatusService.getSmokingStatusByUserId(userId);
        return status.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Helper method để lấy userId từ authentication.
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            System.err.println("Principal type is not CustomUserDetails. Current type: " + authentication.getPrincipal().getClass().getName());
            return null;
        }
    }
}