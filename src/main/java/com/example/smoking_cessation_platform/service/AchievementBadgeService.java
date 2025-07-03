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
    AchievementBadgeRepository achievementBadgeRepository;

    @Autowired
    AchievementBadgeMapper achievementBadgeMapper;

    /**
     * Tạo một huy hiệu mới trong hệ thống.
     *
     * @param request Dữ liệu huy hiệu từ client.
     * @return Đối tượng phản hồi chứa thông tin huy hiệu đã tạo.
     */
    public AchievementBadgeResponse createAchievementBadge(AchievementBadgeRequest request) {
        AchievementBadge badge = achievementBadgeMapper.toEntity(request); // Chuyển DTO thành entity
        badge = achievementBadgeRepository.save(badge); // Lưu vào DB
        return achievementBadgeMapper.toResponse(badge); // Trả kết quả về client
    }

    /**
     * Lấy danh sách tất cả các huy hiệu hiện có trong hệ thống.
     *
     * @return Danh sách phản hồi chứa thông tin các huy hiệu.
     */
    public List<AchievementBadgeResponse> getAllBadge() {
        return achievementBadgeRepository.findAll().stream()
                .map(achievementBadgeMapper::toResponse) // Chuyển từng entity thành response
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin một huy hiệu theo ID.
     *
     * @param id ID của huy hiệu cần cập nhật.
     * @param request Dữ liệu mới từ client.
     * @return Phản hồi chứa thông tin huy hiệu sau khi cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy huy hiệu với ID đã cho.
     */
    public AchievementBadgeResponse updateAchievementBadge(Integer id, AchievementBadgeRequest request) {
        AchievementBadge badge = achievementBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", id));

        // Cập nhật các trường
        badge.setBadgeName(request.getBadgeName());
        badge.setDescription(request.getDescription());
        badge.setCriteria(request.getCriteria());

        return achievementBadgeMapper.toResponse(achievementBadgeRepository.save(badge));
    }

    /**
     * Lấy thông tin chi tiết của một huy hiệu theo ID.
     *
     * @param id ID của huy hiệu cần lấy.
     * @return Phản hồi chứa thông tin chi tiết huy hiệu.
     * @throws ResourceNotFoundException nếu không tìm thấy huy hiệu với ID đã cho.
     */
    public AchievementBadgeResponse getAchievementBadgeById(Integer id) {
        AchievementBadge badge = achievementBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", id));
        return achievementBadgeMapper.toResponse(badge);
    }

    /**
     * Xóa một huy hiệu khỏi hệ thống theo ID.
     *
     * @param id ID của huy hiệu cần xóa.
     * @throws ResourceNotFoundException nếu không tìm thấy huy hiệu với ID đã cho.
     */
    public void deleteAchievementBadgeById(Integer id) {
        AchievementBadge badge = achievementBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", id));
        achievementBadgeRepository.delete(badge);
    }

    /**
     * Lấy thông tin một huy hiệu dựa theo tên huy hiệu.
     *
     * @param name Tên của huy hiệu cần tìm.
     * @return Đối tượng phản hồi chứa thông tin huy hiệu tương ứng với tên đã cho.
     * @throws ResourceNotFoundException nếu không tìm thấy huy hiệu với tên tương ứng.
     */
    public AchievementBadgeResponse getByBadgeName(String name) {
        // Tìm kiếm huy hiệu theo tên, nếu không có sẽ ném ra ngoại lệ
        AchievementBadge badge = achievementBadgeRepository.findByBadgeName(name)
                .orElseThrow(() -> new ResourceNotFoundException("AchievementBadge", name));

        // Chuyển đổi entity sang response DTO để trả về client
        return achievementBadgeMapper.toResponse(badge);
    }
}
