package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.dto.user.UserProfileResponse;
import com.example.smoking_cessation_platform.repository.RoleRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Lấy thông tin hồ sơ người dùng và chuyển đổi sang DTO.
     * Phương thức này được dùng để lấy hồ sơ của bất kỳ người dùng nào theo ID (cho Admin).
     * @param userId ID của người dùng.
     * @return Optional chứa DTO hồ sơ nếu tìm thấy.
     */
    public Optional<UserProfileResponse> getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToUserProfileResponse);
    }

    /**
     * Lấy thông tin hồ sơ của người dùng theo Public ID và chuyển đổi sang DTO.
     * Dùng cho việc hiển thị hồ sơ công khai hoặc tìm kiếm an toàn.
     * @param userPublicId Public ID của người dùng.
     * @return Optional chứa DTO hồ sơ nếu tìm thấy.
     */
    public Optional<UserProfileResponse> getUserProfileByPublicId(String userPublicId) {
        return userRepository.findByUserPublicId(userPublicId)
                .map(this::convertToUserProfileResponse);
    }

    // Các phương thức quản lý dành cho Admin

    /**
     * Lấy danh sách tất cả người dùng trong hệ thống. (Chỉ Admin)
     * @return Danh sách các DTO hồ sơ người dùng.
     */
    public List<UserProfileResponse> getAllUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật vai trò (role) và/hoặc trạng thái (status) của người dùng bởi Admin.
     * @param userId ID của người dùng cần cập nhật.
     * @param newRoleId ID của vai trò mới (tùy chọn).
     * @param newStatus Trạng thái mới (tùy chọn, ví dụ: "active", "inactive").
     * @return Optional chứa DTO hồ sơ người dùng đã cập nhật.
     */
    @Transactional
    public Optional<UserProfileResponse> updateUserRoleAndStatus(Long userId, Integer newRoleId, String newStatus) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (newRoleId != null) {
                        Role newRole = roleRepository.findById(newRoleId)
                                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy vai trò với ID: " + newRoleId));
                        user.setRole(newRole);
                    }
                    if (newStatus != null && !newStatus.isEmpty()) {
                        user.setStatus(newStatus);
                    }
                    User updatedUser = userRepository.save(user);
                    return convertToUserProfileResponse(updatedUser);
                });
    }

    /**
     * Xóa người dùng khỏi hệ thống bởi Admin.
     * @param userId ID của người dùng cần xóa.
     * @return true nếu người dùng đã bị xóa, ngược lại false.
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    /**
     * Phương thức hỗ trợ chuyển đổi từ Entity User sang UserProfileResponse DTO.
     * @param user Entity User.
     * @return UserProfileResponse DTO.
     */
    private UserProfileResponse convertToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .userPublicId(user.getUserPublicId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .registrationDate(user.getRegistrationDate())
                .status(user.getStatus())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .isEmailVerified(user.getIsEmailVerified())
                .authProvider(user.getAuthProvider())
                .build();
    }

    /**
     * Trả về entity User tương ứng với người đang đăng nhập.
     * Ném RuntimeException nếu chưa login hoặc user không tồn tại.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        // Giả sử bạn dùng CustomUserDetails đã lưu userId
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = principal.getUserId();

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với ID: " + currentUserId));
    }


}
