package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.Role;
import com.example.smoking_cessation_platform.dto.user.UserProfileResponse;
import com.example.smoking_cessation_platform.dto.user.UserProfileRequest;
import com.example.smoking_cessation_platform.exception.UserNotFoundException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    public AuthService authService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

    public Optional<UserProfileResponse> getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToUserProfileResponse);
    }

    public List<UserProfileResponse> getListCoachProfile() {
        return roleRepository.findByRoleName("COACH")
                .map(coachRole -> userRepository.findByRole(coachRole).stream()
                        .map(this::convertToUserProfileResponse)
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    public UserProfileResponse getCoachProfileById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToUserProfileResponse)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy coach với ID: " + id));
    }

    public Optional<UserProfileResponse> getUserProfileByPublicId(String userPublicId) {
        return userRepository.findByUserPublicId(userPublicId)
                .map(this::convertToUserProfileResponse);
    }

    public UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest) {
        Optional<User> optionalUser = userRepository.findById(userProfileRequest.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUserName(userProfileRequest.getUsername());

            boolean isEmailChanged = !user.getEmail().equals(userProfileRequest.getEmail());

            user.setEmail(userProfileRequest.getEmail());
            user.setPhone(userProfileRequest.getPhone());
            user.setFullName(userProfileRequest.getFullName());

            if (isEmailChanged){
                user.setIsEmailVerified(false);
                authService.sendEmailVerificationOtp(user);
            }
            userRepository.save(user);
            return convertToUserProfileResponse(user);
        } else {
            throw new UserNotFoundException("Không tìm thấy người dùng với ID: " + userProfileRequest.getUserId());
        }
    }

    public List<UserProfileResponse> getAllUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<UserProfileResponse> updateUserRoleAndStatus(Long userId, Integer newRoleId, String newStatus) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (newRoleId != null) {
                        Role newRole = roleRepository.findById(newRoleId)
                                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy vai trò với ID: " + newRoleId));
                        user.setRole(newRole);
                    }

                    if (newStatus != null && !newStatus.isBlank()) {
                        String lowerStatus = newStatus.trim().toLowerCase();
                        if (!lowerStatus.equals("active") && !lowerStatus.equals("inactive")) {
                            throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận 'active' hoặc 'inactive'.");
                        }
                        user.setStatus(lowerStatus);
                    }

                    User updatedUser = userRepository.save(user);
                    return convertToUserProfileResponse(updatedUser);
                });
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

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

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = principal.getUserId();

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với ID: " + currentUserId));
    }
}