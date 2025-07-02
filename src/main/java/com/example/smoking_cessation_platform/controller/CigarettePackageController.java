package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.dto.CigarettePackage.RecommendationResponse;
import com.example.smoking_cessation_platform.service.CigarettePackageService;
import com.example.smoking_cessation_platform.service.CigaretteRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
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
    private CigarettePackageService cigarettePackageService;

    @Autowired
    private CigaretteRecommendationService cigaretteRecommendationService;

    /**
     * API lấy thông tin chi tiết một gói thuốc.
     * <p>
     * Yêu cầu: <code>GET /api/cigarette-packages/{id}</code>
     * <p>
     * API này cho phép <em>bất kỳ người dùng</em> đã xác thực hoặc chưa xác thực đều truy cập.
     *
     * @param id ID của gói thuốc cần lấy.
     * @return ResponseEntity chứa {@link CigarettePackageDTO} tương ứng.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy chi tiết gói thuốc theo ID",
            description = "Trả về thông tin chi tiết của gói thuốc với ID tương ứng. Có thể truy cập công khai."
    )
    public ResponseEntity<CigarettePackageDTO> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(cigarettePackageService.getByIdDTO(id));
    }

    /**
     * API lấy danh sách tất cả gói thuốc.
     * <p>
     * Yêu cầu: <code>GET /api/cigarette-packages</code>
     *
     * @return ResponseEntity chứa danh sách {@link CigarettePackageDTO}.
     */
    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả gói thuốc",
            description = "Trả về danh sách toàn bộ gói thuốc trong hệ thống."
    )
    public ResponseEntity<List<CigarettePackageDTO>> getAllPackage() {
        return ResponseEntity.ok(cigarettePackageService.getAllPackage());
    }

    /**
     * API tạo mới một gói thuốc.
     * <p>
     * Yêu cầu: <code>POST /api/cigarette-packages</code>
     * <br>Body: <code>CigarettePackageDTO</code> (JSON)
     * <p>
     * <strong>Quyền truy cập:</strong> Chỉ người dùng có vai trò <code>ADMIN</code>.
     *
     * @param dto DTO chứa thông tin gói thuốc cần tạo.
     * @return ResponseEntity chứa {@link CigarettePackageDTO} vừa được tạo.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CigarettePackageDTO> createPackage(@RequestBody CigarettePackageDTO dto) {
        return ResponseEntity.ok(cigarettePackageService.createPackage(dto));
    }

    /**
     * API cập nhật thông tin gói thuốc.
     * <p>
     * Yêu cầu: <code>PUT /api/cigarette-packages/{id}</code>
     * <br>Body: <code>CigarettePackageDTO</code> (JSON)
     * <p>
     * <strong>Quyền truy cập:</strong> Chỉ người dùng có vai trò <code>ADMIN</code>.
     *
     * @param id  ID gói thuốc cần cập nhật.
     * @param dto DTO chứa thông tin mới của gói thuốc.
     * @return ResponseEntity chứa {@link CigarettePackageDTO} sau khi cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CigarettePackageDTO> updatePackage(@PathVariable Long id,
                                                             @RequestBody CigarettePackageDTO dto) {
        return ResponseEntity.ok(cigarettePackageService.updatePackage(id, dto));
    }

    /**
     * API xoá một gói thuốc.
     * <p>
     * Yêu cầu: <code>DELETE /api/cigarette-packages/{id}</code>
     * <p>
     * <strong>Quyền truy cập:</strong> Chỉ người dùng có vai trò <code>ADMIN</code>.
     *
     * @param id ID gói thuốc cần xoá.
     * @return ResponseEntity với mã trạng thái <code>204 No Content</code> khi xoá thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        cigarettePackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * API lấy danh sách gợi ý gói thuốc phù hợp (nhẹ hơn) cho một gói thuốc hiện tại.
     * <p>
     * Yêu cầu: <code>GET /api/cigarette-packages/{id}/recommendations</code>
     *
     * @param id ID gói thuốc gốc.
     * @return ResponseEntity chứa danh sách {@link RecommendationResponse}.
     */
    @GetMapping("/{id}/recommendations")
    @Operation(
            summary = "Lấy danh sách gợi ý cho gói thuốc hiện tại",
            description = "Trả về các gói thuốc nhẹ hơn hoặc phù hợp hơn được khuyến nghị cho gói hiện tại."
    )
    public ResponseEntity<List<RecommendationResponse>> getRecommendationsFrom(@PathVariable Long id) {
        return ResponseEntity.ok(cigaretteRecommendationService.getRecommendationsFrom(id));
    }
}

