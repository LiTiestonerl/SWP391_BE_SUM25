package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.user.AdminUserUpdateRequest;
import com.example.smoking_cessation_platform.dto.user.UserProfileResponse;
import com.example.smoking_cessation_platform.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@SecurityRequirement(name = "api")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * API để lấy danh sách tất cả người dùng trong hệ thống. (Chỉ Admin)
     * Yêu cầu: GET /api/admin/users
     * @return ResponseEntity chứa danh sách DTO hồ sơ người dùng.
     */
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsersForAdmin();
        return ResponseEntity.ok(users);
    }

    /**
     * API để lấy thông tin chi tiết của một người dùng bất kỳ theo ID (chỉ Admin).
     * Yêu cầu: GET /api/admin/users/{userId}
     * @param userId ID của người dùng cần xem.
     * @return ResponseEntity chứa DTO hồ sơ hoặc NOT_FOUND.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long userId) {
        Optional<UserProfileResponse> userProfile = userService.getUserProfile(userId);
        return userProfile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để cập nhật vai trò hoặc trạng thái của một người dùng (chỉ Admin).
     * Yêu cầu: PUT /api/admin/users/{userId}/update-role-status
     * Body: AdminUserUpdateRequest (JSON)
     * @param userId ID của người dùng cần cập nhật.
     * @param request DTO chứa roleId hoặc status mới.
     * @return ResponseEntity chứa DTO hồ sơ đã cập nhật hoặc thông báo lỗi.
     */
    @PutMapping("/{userId}/update-role-status")
    public ResponseEntity<UserProfileResponse> updateUserRoleAndStatus(@PathVariable Long userId,
                                                                       @Valid @RequestBody AdminUserUpdateRequest request) {
        try {
            Optional<UserProfileResponse> updatedProfile = userService.updateUserRoleAndStatus(userId, request.getNewRoleId(), request.getNewStatus());
            return updatedProfile.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * API để xóa một người dùng khỏi hệ thống. (Chỉ Admin)
     * Yêu cầu: DELETE /api/admin/users/{userId}
     * @param userId ID của người dùng cần xóa.
     * @return ResponseEntity chứa NO_CONTENT nếu xóa thành công, hoặc NOT_FOUND.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        }
    }
}
