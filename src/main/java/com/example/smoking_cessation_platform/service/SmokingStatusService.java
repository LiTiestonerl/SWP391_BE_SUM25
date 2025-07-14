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

import java.util.Optional;

@Service
public class SmokingStatusService {

    @Autowired
    private SmokingStatusRepository smokingStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CigarettePackageRepository cigarettePackageRepository;

    /**
     * Tạo smoking status lần đầu cho user (chỉ 1 record/user)
     */
    @Transactional
    public SmokingStatusResponse createSmokingStatus(SmokingStatusRequest createDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        Optional<SmokingStatus> existingStatus = Optional.ofNullable(smokingStatusRepository.findByUser_UserId(userId));
        if (existingStatus.isPresent()) {
            throw new RuntimeException("User đã có smoking profile. Vui lòng dùng API update.");
        }

        CigarettePackage cigarettePackage = null;
        if (createDto.getPackageId() != null) {
            cigarettePackage = cigarettePackageRepository.findById(Long.valueOf(createDto.getPackageId()))
                    .orElseThrow(() -> new RuntimeException("Gói thuốc lá không tồn tại."));
        }

        SmokingStatus smokingStatus = SmokingStatus.builder()
                .cigarettesPerDay(createDto.getCigarettesPerDay())
                .frequency(createDto.getFrequency())
                .preferredFlavor(createDto.getPreferredFlavor())
                .preferredNicotineLevel(createDto.getPreferredNicotineLevel())
                .recordDate(createDto.getRecordDate())
                .user(user)
                .cigarettePackage(cigarettePackage)
                .build();

        SmokingStatus savedStatus = smokingStatusRepository.save(smokingStatus);
        return convertToDto(savedStatus);
    }

    /**
     * Lấy smoking status của user theo userId
     */
    public Optional<SmokingStatusResponse> getSmokingStatusByUserId(Long userId) {
        SmokingStatus smokingStatus = smokingStatusRepository.findByUser_UserId(userId);
        return smokingStatus != null ?
                Optional.of(convertToDto(smokingStatus)) :
                Optional.empty();
    }

    /**
     * Update smoking status của user theo userId
     */
    @Transactional
    public Optional<SmokingStatusResponse> updateSmokingStatusByUserId(Long userId, SmokingStatusRequest updateDto) {
        SmokingStatus existingStatus = smokingStatusRepository.findByUser_UserId(userId);
        if (existingStatus == null) {
            return Optional.empty();
        }

        CigarettePackage cigarettePackage = null;
        if (updateDto.getPackageId() != null) {
            cigarettePackage = cigarettePackageRepository.findById(Long.valueOf(updateDto.getPackageId()))
                    .orElseThrow(() -> new RuntimeException("Gói thuốc lá không tồn tại."));
        }

        existingStatus.setCigarettesPerDay(updateDto.getCigarettesPerDay());
        existingStatus.setFrequency(updateDto.getFrequency());
        existingStatus.setPreferredFlavor(updateDto.getPreferredFlavor());
        existingStatus.setPreferredNicotineLevel(updateDto.getPreferredNicotineLevel());
        existingStatus.setRecordDate(updateDto.getRecordDate());
        existingStatus.setCigarettePackage(cigarettePackage);

        SmokingStatus updatedStatus = smokingStatusRepository.save(existingStatus);
        return Optional.of(convertToDto(updatedStatus));
    }

    /**
     * Xóa smoking status của user theo userId
     */
    @Transactional
    public boolean deleteSmokingStatusByUserId(Long userId) {
        SmokingStatus smokingStatus = smokingStatusRepository.findByUser_UserId(userId);
        if (smokingStatus != null) {
            smokingStatusRepository.delete(smokingStatus);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convert Entity to DTO
     */
    private SmokingStatusResponse convertToDto(SmokingStatus smokingStatus) {
        return SmokingStatusResponse.builder()
                .statusId(smokingStatus.getStatusId())
                .cigarettesPerDay(smokingStatus.getCigarettesPerDay())
                .frequency(smokingStatus.getFrequency())
                .preferredFlavor(smokingStatus.getPreferredFlavor())
                .preferredNicotineLevel(smokingStatus.getPreferredNicotineLevel())
                .recordDate(smokingStatus.getRecordDate())
                .userId(smokingStatus.getUser().getUserId())
                .cigarettePackageId(smokingStatus.getCigarettePackage() != null ? smokingStatus.getCigarettePackage().getCigaretteId() : null)
                .cigarettePackageName(smokingStatus.getCigarettePackage() != null ? smokingStatus.getCigarettePackage().getCigaretteName() : null)
                .build();
    }
}
