package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.user.UserProfileResponse;
import com.example.smoking_cessation_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
public class CoachController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllCoach() {
        List<UserProfileResponse> coachProfile = userService.getListCoachProfile();
        if (coachProfile.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(coachProfile);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserProfileResponse> getCoachById(@PathVariable Long id) {
        UserProfileResponse coachProfile = userService.getCoachProfileById(id);
        if (coachProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coachProfile);
    }

}
