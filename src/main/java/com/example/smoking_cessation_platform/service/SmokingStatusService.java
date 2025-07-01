package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.SmokingStatus;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.dto.smokingstatus.SmokingStatusRequest;
import com.example.smoking_cessation_platform.dto.smokingstatus.SmokingStatusResponse;
import com.example.smoking_cessation_platform.repository.SmokingStatusRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SmokingStatusService {

    @Autowired
    private SmokingStatusRepository smokingStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CigarettePackageRepository cigarettePackageRepository;

    /**
     * Tạo một bản ghi thói quen hút thuốc mới.
     * @param createDto DTO chứa thông tin bản ghi cần tạo.
     * @param userId ID của người dùng tạo bản ghi (lấy từ ngữ cảnh bảo mật).
     * @return DTO phản hồi của bản ghi đã được tạo.
     */
    @Transactional
    public SmokingStatusResponse createSmokingStatus(SmokingStatusRequest createDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));


        if (smokingStatusRepository.findByUserAndRecordDate(user, createDto.getRecordDate()).isPresent()) {
            throw new RuntimeException("Đã có bản ghi thói quen hút thuốc cho ngày này. Vui lòng cập nhật thay vì tạo mới.");
        }

        CigarettePackage cigarettePackage = null;
        if (createDto.getPackageId() != null) {
            cigarettePackage = cigarettePackageRepository.findById(createDto.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Gói thuốc lá không tồn tại."));
        }

        SmokingStatus smokingStatus = SmokingStatus.builder()
                .cigarettesPerDay(createDto.getCigarettesPerDay())
                .frequency(createDto.getFrequency())
                .pricePerPack(createDto.getPricePerPack())
                .recordDate(createDto.getRecordDate())
                .user(user)
                .cigarettePackage(cigarettePackage)
                .build();

        SmokingStatus savedStatus = smokingStatusRepository.save(smokingStatus);
        return convertToDto(savedStatus);
    }

    /**
     * Lấy tất cả các bản ghi thói quen hút thuốc của một người dùng cụ thể.
     * @param userId ID của người dùng.
     * @return Danh sách các DTO phản hồi bản ghi thói quen hút thuốc.
     */
    public List<SmokingStatusResponse> getAllSmokingStatusesByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));
        return smokingStatusRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một bản ghi thói quen hút thuốc theo ID.
     * @param statusId ID của bản ghi.
     * @return Optional chứa DTO phản hồi bản ghi (nếu tìm thấy).
     */
    public Optional<SmokingStatusResponse> getSmokingStatusById(Integer statusId) {
        return smokingStatusRepository.findById(statusId)
                .map(this::convertToDto);
    }

    /**
     * Cập nhật thông tin một bản ghi thói quen hút thuốc.
     * @param statusId ID của bản ghi cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @param currentUserId ID của người dùng hiện tại (để kiểm tra quyền sở hữu).
     * @return Optional chứa DTO phản hồi bản ghi đã cập nhật (nếu tìm thấy).
     */
    @Transactional
    public Optional<SmokingStatusResponse> updateSmokingStatus(Integer statusId, SmokingStatusRequest updateDto, Long currentUserId) {
        return smokingStatusRepository.findById(statusId)
                .map(existingStatus -> {
                    // nhớ kiểm tra phân quyền ở đây
                    if (!existingStatus.getUser().getUserId().equals(currentUserId)) {
                        throw new RuntimeException("Bạn không có quyền sửa bản ghi này.");
                    }

                    if (!existingStatus.getRecordDate().equals(updateDto.getRecordDate())) {
                        User user = existingStatus.getUser();
                        if (smokingStatusRepository.findByUserAndRecordDate(user, updateDto.getRecordDate()).isPresent()) {
                            throw new RuntimeException("Đã có bản ghi thói quen hút thuốc cho ngày " + updateDto.getRecordDate() + ". Vui lòng chọn ngày khác.");
                        }
                    }

                    CigarettePackage cigarettePackage = null;
                    if (updateDto.getPackageId() != null) {
                        cigarettePackage = cigarettePackageRepository.findById(updateDto.getPackageId())
                                .orElseThrow(() -> new RuntimeException("Gói thuốc lá không tồn tại."));
                    }

                    existingStatus.setCigarettesPerDay(updateDto.getCigarettesPerDay());
                    existingStatus.setFrequency(updateDto.getFrequency());
                    existingStatus.setPricePerPack(updateDto.getPricePerPack());
                    existingStatus.setRecordDate(updateDto.getRecordDate());
                    existingStatus.setCigarettePackage(cigarettePackage);

                    SmokingStatus updatedStatus = smokingStatusRepository.save(existingStatus);
                    return convertToDto(updatedStatus);
                });
    }

    /**
     * Xóa một bản ghi thói quen hút thuốc.
     * @param statusId ID của bản ghi cần xóa.
     * @param currentUserId ID của người dùng hiện tại (để kiểm tra quyền sở hữu).
     * @return true nếu xóa thành công, false nếu không tìm thấy bản ghi.
     */
    @Transactional
    public boolean deleteSmokingStatus(Integer statusId, Long currentUserId) {
        return smokingStatusRepository.findById(statusId)
                .map(smokingStatus -> {
                    // nhớ phân quyền ở đây
                    if (!smokingStatus.getUser().getUserId().equals(currentUserId)) {
                        throw new RuntimeException("Bạn không có quyền xóa bản ghi này.");
                    }
                    smokingStatusRepository.delete(smokingStatus);
                    return true;
                })
                .orElse(false);
    }



    /**
     * Phương thức hỗ trợ chuyển đổi từ Entity SmokingStatus sang SmokingStatusResponseDTO.
     * @param smokingStatus Entity SmokingStatus.
     * @return SmokingStatusResponseDTO.
     */
    private SmokingStatusResponse convertToDto(SmokingStatus smokingStatus) {
        return SmokingStatusResponse.builder()
                .statusId(smokingStatus.getStatusId())
                .cigarettesPerDay(smokingStatus.getCigarettesPerDay())
                .frequency(smokingStatus.getFrequency())
                .pricePerPack(smokingStatus.getPricePerPack())
                .recordDate(smokingStatus.getRecordDate())
                .userId(smokingStatus.getUser().getUserId())
                .userName(smokingStatus.getUser().getUserName())
                .userFullName(smokingStatus.getUser().getFullName())
                .userEmail(smokingStatus.getUser().getEmail())
                .userPhone(smokingStatus.getUser().getPhone())
                .userRegistrationDate(smokingStatus.getUser().getRegistrationDate())
                .packageId(smokingStatus.getCigarettePackage() != null ? smokingStatus.getCigarettePackage().getCigaretteId() : null)
                .packageName(smokingStatus.getCigarettePackage() != null ? smokingStatus.getCigarettePackage().getCigaretteName() : null)
                .build();
    }
}
