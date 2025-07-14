package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeRequest;
import com.example.smoking_cessation_platform.dto.achievementbadge.AchievementBadgeResponse;
import com.example.smoking_cessation_platform.entity.AchievementBadge;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.mapper.AchievementBadgeMapper;
import com.example.smoking_cessation_platform.repository.AchievementBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AchievementBadgeService {


    @Autowired
    AchievementBadgeRepository achievementBadgeRepository; // Repository thao tác DB cho bảng AchievementBadge

    @Autowired
    AchievementBadgeMapper achievementBadgeMapper; // Mapper chuyển đổi giữa DTO và entity

    /**
     * Tạo một huy hiệu mới.
     *
     * @param request Dữ liệu từ client (tên, mô tả, tiêu chí).
     * @return DTO phản hồi sau khi lưu thành công.
     */
    public AchievementBadgeResponse createAchievementBadge(AchievementBadgeRequest request) {
        AchievementBadge badge = achievementBadgeMapper.toEntity(request); // Chuyển DTO thành entity
        badge = achievementBadgeRepository.save(badge); // Lưu vào DB
        return achievementBadgeMapper.toResponse(badge); // Trả DTO phản hồi
    }

    /**
     * Lấy danh sách tất cả huy hiệu chưa bị xóa.
     *
     * @return Danh sách DTO các huy hiệu.
     */
    public List<AchievementBadgeResponse> getAllBadge() {
        return achievementBadgeRepository.findAllByDeletedFalse().stream() // Chỉ lấy badge chưa bị đánh dấu xóa
                .map(achievementBadgeMapper::toResponse) // Chuyển sang DTO phản hồi
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin một huy hiệu dựa trên ID.
     *
     * @param id ID huy hiệu.
     * @param request Thông tin mới từ client.
     * @return DTO phản hồi sau khi cập nhật.
     * @throws ResourceNotFoundException Nếu không tìm thấy huy hiệu.
     */
    public AchievementBadgeResponse updateAchievementBadge(Integer id, AchievementBadgeRequest request) {
        AchievementBadge badge = achievementBadgeRepository.findByBadgeIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", id)); // Kiểm tra tồn tại

        // Cập nhật thông tin từ request
        badge.setBadgeName(request.getBadgeName());
        badge.setDescription(request.getDescription());
        badge.setCriteria(request.getCriteria());

        return achievementBadgeMapper.toResponse(achievementBadgeRepository.save(badge)); // Lưu và trả về kết quả
    }

    /**
     * Lấy chi tiết một huy hiệu theo ID.
     *
     * @param id ID huy hiệu.
     * @return DTO phản hồi chứa thông tin chi tiết.
     * @throws ResourceNotFoundException Nếu không tồn tại.
     */
    public AchievementBadgeResponse getAchievementBadgeById(Integer id) {
        AchievementBadge badge = achievementBadgeRepository.findByBadgeIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", id));
        return achievementBadgeMapper.toResponse(badge);
    }

    /**
     * "Xóa mềm" một huy hiệu theo ID (đánh dấu là đã bị xóa thay vì xóa thật).
     *
     * @param id ID huy hiệu.
     * @throws ResourceNotFoundException Nếu không tìm thấy.
     */
    public void deleteAchievementBadgeById(Integer id) {
        AchievementBadge badge = achievementBadgeRepository.findByBadgeIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", id));

        badge.setDeleted(true); // Đánh dấu là đã bị xóa (xóa mềm)
        achievementBadgeRepository.save(badge); // Lưu cập nhật
    }

    /**
     * Tìm huy hiệu theo tên.
     *
     * @param name Tên huy hiệu.
     * @return DTO phản hồi thông tin huy hiệu.
     * @throws ResourceNotFoundException Nếu không tìm thấy.
     */
    public AchievementBadgeResponse getByBadgeName(String name) {
        AchievementBadge badge = achievementBadgeRepository.findByBadgeNameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", name));
        return achievementBadgeMapper.toResponse(badge);
    }
}
