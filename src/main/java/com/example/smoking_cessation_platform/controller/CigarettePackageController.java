package com.example.smoking_cessation_platform.controller;


import com.example.smoking_cessation_platform.dto.cigarettePackage.CigarettePackageResponse;
import com.example.smoking_cessation_platform.dto.cigarettePackage.CigarettePackagerequest;
import com.example.smoking_cessation_platform.service.CigarettePackageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cigarette-packages")
@SecurityRequirement(name = "api")
public class CigarettePackageController {

    @Autowired
    private CigarettePackageService cigarettePackageService;

    /**
     * API để tạo một gói thuốc lá mới.
     * Yêu cầu: POST /api/cigarette-packages
     * Body: CigarettePackagerequest (JSON)
     *
     * @param createDto DTO chứa thông tin gói thuốc lá cần tạo.
     * @return ResponseEntity chứa DTO phản hồi của gói thuốc lá đã được tạo.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CigarettePackageResponse> createCigarettePackage(CigarettePackagerequest createDto) {
        CigarettePackageResponse newPackage = cigarettePackageService.createCigarettePackage(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPackage);
    }

    /**
     * API để cập nhật thông tin gói thuốc lá.
     * Yêu cầu: PUT /api/cigarette-packages/{cigaretteId}
     * Body: CigarettePackagerequest (JSON)
     *
     * @param cigaretteId ID của gói thuốc lá cần cập nhật.
     * @param updateDto   DTO chứa thông tin cập nhật gói thuốc lá.
     * @return ResponseEntity chứa DTO phản hồi của gói thuốc lá đã được cập nhật, hoặc NOT_FOUND nếu không tìm thấy gói thuốc lá.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{cigaretteId}")
    public ResponseEntity<CigarettePackageResponse> updateCigarettePackage(@PathVariable Long cigaretteId, @Valid @RequestBody CigarettePackagerequest updateDto) {
        Optional<CigarettePackageResponse> updatedPackage = cigarettePackageService.updateCigarettePackage(cigaretteId, updateDto);
        return updatedPackage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * API để xóa một gói thuốc lá.
     * Yêu cầu: DELETE /api/cigarette-packages/{cigaretteId}
     *
     * @param cigaretteId ID của gói thuốc lá cần xóa.
     * @return ResponseEntity chứa thông báo thành công hoặc lỗi nếu không tìm thấy gói thuốc lá.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{cigaretteId}")
    public ResponseEntity<String> deleteCigarettePackage(@PathVariable Long cigaretteId) {
        try {
            cigarettePackageService.deleteCigarettePackage(cigaretteId);
            return ResponseEntity.ok("Gói thuốc lá đã được xóa thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy gói thuốc lá với ID: " + cigaretteId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xóa gói thuốc lá.");
        }
    }

    /**
     * API để lấy một gói thuốc lá theo ID.
     * Yêu cầu: GET /api/cigarette-packages/{cigaretteId}
     *
     * @param cigaretteId ID của gói thuốc lá cần lấy.
     * @return ResponseEntity chứa DTO phản hồi của gói thuốc lá hoặc NOT_FOUND nếu không tìm thấy.
     */
    @GetMapping({"/{cigaretteId}"})
    public ResponseEntity<CigarettePackageResponse> getCigarettePackageById(@PathVariable Long cigaretteId) {
        Optional<CigarettePackageResponse> cigarettePackage = cigarettePackageService.getCigarettePackageById(cigaretteId);
        return cigarettePackage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * API để lấy tất cả các gói thuốc lá.
     * Yêu cầu: GET /api/cigarette-packages
     *
     * @return ResponseEntity chứa danh sách DTO phản hồi của tất cả gói thuốc lá.
     */
    @GetMapping
    public ResponseEntity<?> getAllCigarettePackages() {
        try {
            return ResponseEntity.ok(cigarettePackageService.getAllCigarettePackages());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy danh sách gói thuốc lá.");
        }
    }


}
