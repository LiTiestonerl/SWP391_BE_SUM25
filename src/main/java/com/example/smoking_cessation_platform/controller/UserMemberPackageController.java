package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.usermemberpackage.UserMemberPackageResponse;
import com.example.smoking_cessation_platform.security.CustomUserDetails;
import com.example.smoking_cessation_platform.service.UserMemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-membership")
@SecurityRequirement(name = "api")
public class UserMemberPackageController {
    @Autowired
    private UserMemberService userMemberService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserMemberPackageResponse> getMyCurrentPackage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userMemberService.getCurrentPackage(userDetails.getUserId()));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelMyCurrentPackage(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userMemberService.cancelCurrentPackage(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-coach")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> assignCoachToMyPackage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long coachId
    ) {
        userMemberService.assignCoachToUserPackage(userDetails.getUserId(), coachId);
        return ResponseEntity.ok("Gán coach thành công cho gói hiện tại.");
    }
}
