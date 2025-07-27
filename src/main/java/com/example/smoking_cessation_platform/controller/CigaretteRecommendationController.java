package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.cigaretteRecommendation.CigaretteRecommendationResponse;
import com.example.smoking_cessation_platform.service.CigaretteRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cigarette-recommendations")
@CrossOrigin(origins = "*")
public class CigaretteRecommendationController {

    @Autowired
    private CigaretteRecommendationService recommendationService;

    @GetMapping("/{recId}")
    public ResponseEntity<CigaretteRecommendationResponse> getRecommendationById(@PathVariable Integer recId) {
        Optional<CigaretteRecommendationResponse> response = recommendationService.getRecommendationById(recId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/for-cigarette/{cigaretteId}")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getRecommendationsForCigarette(
            @PathVariable Long cigaretteId) {
        List<CigaretteRecommendationResponse> recommendations =
                recommendationService.getRecommendationsByFromPackage(cigaretteId);
        return ResponseEntity.ok(recommendations);
    }

    // Added endpoints for specialized recommendation strategies
    @GetMapping("/lighter-nicotine/{cigaretteId}")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getLighterNicotineRecommendations(
            @PathVariable Long cigaretteId) {
        List<CigaretteRecommendationResponse> recommendations =
                recommendationService.getLighterNicotineRecommendations(cigaretteId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/same-flavor/{cigaretteId}")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getSameFlavorRecommendations(
            @PathVariable Long cigaretteId) {
        List<CigaretteRecommendationResponse> recommendations =
                recommendationService.getSameFlavorRecommendations(cigaretteId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/same-brand-lighter/{cigaretteId}")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getSameBrandLighterRecommendations(
            @PathVariable Long cigaretteId) {
        List<CigaretteRecommendationResponse> recommendations =
                recommendationService.getSameBrandLighterRecommendations(cigaretteId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/best/{cigaretteId}")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getBestRecommendations(
            @PathVariable Long cigaretteId) {
        List<CigaretteRecommendationResponse> recommendations =
                recommendationService.getBestRecommendations(cigaretteId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/by-smoking-status/{smokingStatusId}")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getRecommendationsBySmokingStatus(
            @PathVariable Long smokingStatusId) {
        List<CigaretteRecommendationResponse> recommendations =
                recommendationService.getRecommendationsBySmokingStatus(smokingStatusId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CigaretteRecommendationResponse>> getAllActiveRecommendations() {
        try {
            List<CigaretteRecommendationResponse> recommendations =
                    recommendationService.getAllActiveRecommendations();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin endpoints
    @PatchMapping("/admin/{recId}/toggle-active")
    public ResponseEntity<CigaretteRecommendationResponse> toggleActiveStatus(@PathVariable Integer recId) {
        try {
            CigaretteRecommendationResponse response = recommendationService.toggleActiveStatus(recId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/admin/{recId}/priority")
    public ResponseEntity<CigaretteRecommendationResponse> updatePriority(
            @PathVariable Integer recId,
            @RequestParam Integer priority) {
        try {
            CigaretteRecommendationResponse response = recommendationService.updatePriority(recId, priority);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}