package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.service.CigarettePackageService;
import com.example.smoking_cessation_platform.service.CigaretteRecommendationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cigarette-packages")
@SecurityRequirement(name = "api")
public class CigarettePackageController {

    @Autowired
    CigarettePackageService cigarettePackageService;

    @Autowired
    CigaretteRecommendationService cigaretteRecommendationService;

    @GetMapping("/{id}")
    public ResponseEntity<CigarettePackageDTO> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(cigarettePackageService.getByIdDTO(id));
    }

    @GetMapping
    public ResponseEntity<List<CigarettePackageDTO>> getAllPackage(){
        return ResponseEntity.ok(cigarettePackageService.getAllPackage());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CigarettePackageDTO> createPackage(@RequestBody CigarettePackageDTO dto){
        return ResponseEntity.ok(cigarettePackageService.createPackage(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CigarettePackageDTO> updatePackage(@PathVariable Long id, @RequestBody CigarettePackageDTO dto) {
        return ResponseEntity.ok(cigarettePackageService.updatePackage(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePackage (@PathVariable Long id){
        cigarettePackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<RecommendationResponse>> getRecommendationsFrom(@PathVariable Long id) {
        return ResponseEntity.ok(cigaretteRecommendationService.getRecommendationsFrom(id));
    }



}
