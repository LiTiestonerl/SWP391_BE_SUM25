package com.example.smoking_cessation_platform.controller;

import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeRequest;
import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeResponse;
import com.example.smoking_cessation_platform.service.AchievementBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievement-badge")
@SecurityRequirement(name = "api")
public class AchievementBadgeController {

    @Autowired
    AchievementBadgeService achievementBadgeService;

    /**
     * API để tạo một huy hiệu mới.
     * Yêu cầu: POST /api/achievement-badges
     * @param request Dữ liệu huy hiệu từ phía client (tên, mô tả, tiêu chí).
     * @return ResponseEntity chứa thông tin huy hiệu vừa được tạo.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AchievementBadgeResponse> createBadge(@RequestBody AchievementBadgeRequest request){
        return ResponseEntity.ok(achievementBadgeService.createAchievementBadge(request));
    }

    /**
     * API để lấy danh sách tất cả các huy hiệu.
     * Yêu cầu: GET /api/achievement-badges
     * @return ResponseEntity chứa danh sách tất cả huy hiệu trong hệ thống.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách tất cả huy hiệu", description = "Trả về danh sách tất cả huy hiệu hiện có trong hệ thống.")
    public ResponseEntity<?> getAllBadge(){
        List<AchievementBadgeResponse> responses = achievementBadgeService.getAllBadge();
        return ResponseEntity.ok(responses);
    }

    /**
     * API để lấy thông tin chi tiết của một huy hiệu theo ID.
     * Yêu cầu: GET /api/achievement-badges/{id}
     * @param id ID của huy hiệu cần lấy thông tin.
     * @return ResponseEntity chứa thông tin chi tiết của huy hiệu.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy huy hiệu theo ID", description = "Trả về thông tin chi tiết của một huy hiệu dựa theo ID.")
    public ResponseEntity<AchievementBadgeResponse> getAchievementBadgeById(@PathVariable Integer id){
        AchievementBadgeResponse response = achievementBadgeService.getAchievementBadgeById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * API để lấy huy hiệu theo tên.
     * Yêu cầu: GET /api/achievement-badges/by-name?name={badgeName}
     * @param  name Tên huy hiệu cần tìm.
     * @return Thông tin huy hiệu nếu tìm thấy.
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "Lấy huy hiệu theo tên", description = "Trả về thông tin huy hiệu dựa theo tên cụ thể.")
    public ResponseEntity<AchievementBadgeResponse> getAchievementBadgeByName(@RequestParam String name) {
        return ResponseEntity.ok(achievementBadgeService.getByBadgeName(name));
    }

    /**
     * API để cập nhật thông tin một huy hiệu theo ID.
     * Yêu cầu: PUT /api/achievement-badges/{id}
     * @param id ID của huy hiệu cần cập nhật.
     * @param request Dữ liệu cập nhật từ client.
     * @return ResponseEntity chứa thông tin huy hiệu sau khi cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AchievementBadgeResponse> updateAchievementBadge(@PathVariable Integer id,
                                                                           @RequestBody AchievementBadgeRequest request){
        AchievementBadgeResponse response = achievementBadgeService.updateAchievementBadge(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * API để xóa một huy hiệu khỏi hệ thống.
     * Yêu cầu: DELETE /api/achievement-badges/{id}
     * @param id ID của huy hiệu cần xóa.
     * @return ResponseEntity chứa NO_CONTENT nếu xóa thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAchievementBadgeById(@PathVariable Integer id){
        achievementBadgeService.deleteAchievementBadgeById(id);
        return ResponseEntity.noContent().build();
    }
}

