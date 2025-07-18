package com.example.smoking_cessation_platform.controller;

    import com.example.smoking_cessation_platform.dto.user.UserProfileRequest;
    import com.example.smoking_cessation_platform.dto.user.UserProfileResponse;
    import com.example.smoking_cessation_platform.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/user")
    public class UserProfileController {

        @Autowired
        private UserService userService;

        @PutMapping("/profile")
        public ResponseEntity<UserProfileResponse> updateUserProfile(@RequestBody UserProfileRequest userProfileRequest) {
            try {
                UserProfileResponse response = userService.updateUserProfile(userProfileRequest);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @GetMapping("/profile/{userId}")
        public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
            return userService.getUserProfile(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/profile/public/{userPublicId}")
        public ResponseEntity<UserProfileResponse> getUserProfileByPublicId(@PathVariable String userPublicId) {
            return userService.getUserProfileByPublicId(userPublicId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }