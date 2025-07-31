package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.quitplan.QuitProgressRequest;
import com.example.smoking_cessation_platform.dto.quitplan.QuitProgressResponse;
import com.example.smoking_cessation_platform.service.QuitProgressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quit-progress")
@SecurityRequirement(name = "api")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class QuitProgressController {

    @Autowired
    private QuitProgressService quitProgressService;

    @PostMapping("/update")
    public ResponseEntity<QuitProgressResponse> updateProgress(@RequestBody QuitProgressRequest request) {
        QuitProgressResponse response = quitProgressService.updateProgress(request);
        return ResponseEntity.ok(response);
    }

}
