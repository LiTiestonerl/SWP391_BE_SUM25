package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.memberPackage.MemberPackageRequest;
import com.example.smoking_cessation_platform.dto.memberPackage.MemberPackageResponse;
import com.example.smoking_cessation_platform.service.MemberPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member-packages")
@SecurityRequirement(name = "api")
public class MemberPackageController {

    @Autowired
    private MemberPackageService memberPackageService;

    /**
     * API để tạo một gói đăng ký mới.
     * Yêu cầu: POST /api/member-packages
     * Body: MemberPackageRequest (JSON)
     * @param createDto DTO chứa thông tin gói cần tạo.
     * @return ResponseEntity chứa DTO phản hồi hoặc thông báo lỗi.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MemberPackageResponse> createMemberPackage(@Valid @RequestBody MemberPackageRequest createDto) {
        MemberPackageResponse newPackage = memberPackageService.createMemberPackage(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPackage);
    }

    /**
     * API để lấy tất cả các gói đăng ký.
     * Yêu cầu: GET /api/member-packages
     * @return ResponseEntity chứa danh sách DTO phản hồi.
     */
    @GetMapping
    @PermitAll
    @Operation(summary = "Xem danh sách tất cả gói", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<List<MemberPackageResponse>> getAllMemberPackages() {
        List<MemberPackageResponse> packages = memberPackageService.getAllMemberPackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * API để lấy một gói đăng ký theo ID.
     * Yêu cầu: GET /api/member-packages/{id}
     * @param id ID của gói đăng ký.
     * @return ResponseEntity chứa DTO phản hồi hoặc NOT_FOUND nếu không tìm thấy.
     */
    @GetMapping("/{id}")
    @PermitAll
    @Operation(summary = "Xem chi tiết 1 gói", security = @SecurityRequirement(name = "none"))
    public ResponseEntity<MemberPackageResponse> getMemberPackageById(@PathVariable Integer id) {
        Optional<MemberPackageResponse> memberPackage = memberPackageService.getMemberPackageById(id);
        return memberPackage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để cập nhật thông tin một gói đăng ký.
     * Yêu cầu: PUT /api/member-packages/{id}
     * Body: MemberPackageRequest (JSON)
     * @param id ID của gói đăng ký cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @return ResponseEntity chứa DTO phản hồi hoặc NOT_FOUND nếu không tìm thấy.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MemberPackageResponse> updateMemberPackage(@PathVariable Integer id, @Valid @RequestBody MemberPackageRequest updateDto) {
        Optional<MemberPackageResponse> updatedPackage = memberPackageService.updateMemberPackage(id, updateDto);
        return updatedPackage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để xóa một gói đăng ký theo ID.
     * Yêu cầu: DELETE /api/member-packages/{id}
     * @param id ID của gói đăng ký cần xóa.
     * @return ResponseEntity chứa NO_CONTENT nếu xóa thành công, hoặc NOT_FOUND nếu không tìm thấy.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberPackage(@PathVariable Integer id) {
        boolean deleted = memberPackageService.deleteMemberPackage(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 NO_CONTENT
        } else {
            return ResponseEntity.notFound().build(); // 404 NOT_FOUND
        }
    }
}
