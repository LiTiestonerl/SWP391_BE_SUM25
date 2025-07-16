package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.SmokingStatus;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.dto.smokingstatus.SmokingStatusRequest;
import com.example.smoking_cessation_platform.dto.smokingstatus.SmokingStatusResponse;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.repository.SmokingStatusRepository;
import com.example.smoking_cessation_platform.repository.UserRepository;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
     * Tạo smoking status lần đầu cho user (chỉ 1 record/user)
     */
    @Transactional
    public SmokingStatusResponse createSmokingStatus(SmokingStatusRequest createDto, Long userId) {
        // Lấy user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Tìm hoặc tạo mới gói thuốc
        CigarettePackage cigarettePackage = null;

        if (createDto.getPackageId() != null) {
            cigarettePackage = cigarettePackageRepository.findById(createDto.getPackageId()).orElse(null);
        }

        // Nếu không có hoặc không tồn tại gói → tự tạo mới dựa vào sở thích
        if (cigarettePackage == null) {
            cigarettePackage = new CigarettePackage();

            cigarettePackage.setBrand("Generated Package");

            String flavorName = switch (createDto.getPreferredFlavor()) {
                case "MENTHOL" -> "Menthol";
                case "VANILLA" -> "Vanilla";
                case "CHERRY" -> "Cherry";
                case "CHOCOLATE" -> "Chocolate";
                case "ORIGINAL" -> "Original";
                case "MINT" -> "Mint";
                default -> createDto.getPreferredFlavor();
            };

            String nicotineLevel = switch (createDto.getPreferredNicotineLevel()) {
                case "HIGH" -> "High Nicotine";
                case "MEDIUM" -> "Medium Nicotine";
                case "LOW" -> "Low Nicotine";
                case "ZERO" -> "Zero Nicotine";
                default -> createDto.getPreferredNicotineLevel();
            };

            cigarettePackage.setCigaretteName(flavorName + " Flavor - " + nicotineLevel);
            cigarettePackage.setFlavor(createDto.getPreferredFlavor());
            cigarettePackage.setNicoteneStrength(createDto.getPreferredNicotineLevel());

            cigarettePackage.setNicotineMg(switch (createDto.getPreferredNicotineLevel()) {
                case "HIGH" -> 2.0;
                case "MEDIUM" -> 1.0;
                case "LOW" -> 0.5;
                case "ZERO" -> 0.0;
                default -> 0.0;
            });

            cigarettePackage.setSticksPerPack(20);
            cigarettePackage.setPrice(BigDecimal.valueOf(30000)); // mặc định giá

            cigarettePackage = cigarettePackageRepository.save(cigarettePackage);
        }

        // Tạo đối tượng SmokingStatus
        SmokingStatus status = new SmokingStatus();
        status.setUser(user);
        status.setCigarettesPerDay(createDto.getCigarettesPerDay());
        status.setFrequency(createDto.getFrequency());
        status.setPreferredFlavor(createDto.getPreferredFlavor());
        status.setPreferredNicotineLevel(createDto.getPreferredNicotineLevel());
        status.setRecordDate(createDto.getRecordDate());
        status.setCigarettePackage(cigarettePackage);

        // Lưu lại
        smokingStatusRepository.save(status);

        // Trả về response
        return SmokingStatusResponse.builder()
                .statusId(status.getStatusId())
                .cigarettesPerDay(status.getCigarettesPerDay())
                .frequency(status.getFrequency())
                .preferredFlavor(status.getPreferredFlavor())
                .preferredNicotineLevel(status.getPreferredNicotineLevel())
                .recordDate(status.getRecordDate())
                .userId(user.getUserId())
                .cigarettePackageId(cigarettePackage.getCigaretteId())
                .cigarettePackageName(cigarettePackage.getCigaretteName())
                .build();
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
