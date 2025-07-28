package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageResponse;
import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackagerequest;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import com.example.smoking_cessation_platform.repository.CigaretteRecommendationRepository;
import com.example.smoking_cessation_platform.repository.SmokingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CigarettePackageService {

    @Autowired
    private CigarettePackageRepository cigarettePackageRepository;

    @Autowired
    private SmokingStatusRepository smokingStatusRepository;

    @Autowired
    private CigaretteRecommendationRepository cigaretteRecommendationRepository;


    /**
     * Tạo một gói thuốc lá mới.
     * @param createDto DTO chứa thông tin gói thuốc lá cần tạo.
     * @return DTO phản hồi của gói thuốc lá đã được tạo.
     */
    @Transactional
    public CigarettePackageResponse createCigarettePackage(CigarettePackagerequest createDto) {
        if (cigarettePackageRepository.existsByCigaretteName(createDto.getCigaretteName())) {
            throw new RuntimeException("Gói thuốc lá với tên này đã tồn tại.");
        }

        CigarettePackage cigarettePackage = CigarettePackage.builder()
                .cigaretteName(createDto.getCigaretteName())
                .brand(createDto.getCigaretteBrand())
                .flavor(createDto.getFlavor())
                .nicoteneStrength(createDto.getNicoteneStrength())
                .sticksPerPack(createDto.getSticksPerPack())
                .price(createDto.getPrice())
                .build();

        CigarettePackage savedPackage = cigarettePackageRepository.save(cigarettePackage);

        return convertToDto(savedPackage);
    }

    /**
     * Cập nhật thông tin gói thuốc lá.
     * @param cigaretteId ID của gói thuốc lá cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật gói thuốc lá.
     * @return Optional chứa DTO phản hồi của gói thuốc lá đã được cập nhật, hoặc rỗng nếu không tìm thấy gói thuốc lá.
     */
    @Transactional
    public Optional<CigarettePackageResponse> updateCigarettePackage(Long cigaretteId, CigarettePackagerequest updateDto) {
        return cigarettePackageRepository.findById(cigaretteId)
                .map(existingPackage -> {
                    existingPackage.setCigaretteName(updateDto.getCigaretteName());
                    existingPackage.setBrand(updateDto.getCigaretteBrand());
                    existingPackage.setFlavor(updateDto.getFlavor());
                    existingPackage.setNicoteneStrength(updateDto.getNicoteneStrength());
                    existingPackage.setSticksPerPack(updateDto.getSticksPerPack());
                    existingPackage.setPrice(updateDto.getPrice());
                    CigarettePackage updatedPackage = cigarettePackageRepository.save(existingPackage);
                    return convertToDto(updatedPackage);
                });
    }


    @Transactional
    public boolean deleteCigarettePackage(Long cigaretteId) {
        if (!cigarettePackageRepository.existsById(cigaretteId)) {
            return false;
        }
        cigaretteRecommendationRepository.deleteByToPackage_CigaretteId(cigaretteId);// xoá relate liên quan đến recommendation
        cigaretteRecommendationRepository.deleteByFromPackage_CigaretteId(cigaretteId);// xoá relate liên quan đến recommendation
        cigarettePackageRepository.deleteById(cigaretteId);
        return true;
    }

    /**
     * Lấy tất cả các gói thuốc lá.
     * @return Danh sách các DTO phản hồi gói thuốc lá.
     */
    public List<CigarettePackageResponse> getAllCigarettePackages() {
        return cigarettePackageRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một gói thuốc lá theo ID.
     * @param cigaretteId ID của gói thuốc lá cần lấy.
     * @return Optional chứa DTO phản hồi của gói thuốc lá, hoặc rỗng nếu không tìm thấy.
     */
    public Optional<CigarettePackageResponse> getCigarettePackageById(Long cigaretteId) {
        return cigarettePackageRepository.findById(cigaretteId)
                .map(this::convertToDto);
    }


    /**
     * Phương thức hỗ trợ chuyển đổi từ Entity CigarettePackage sang CigarettePackageResponse.
     * @param cigarettePackage Entity CigarettePackage.
     * @return CigarettePackageResponse.
     */
    private CigarettePackageResponse convertToDto(CigarettePackage cigarettePackage) {
        return CigarettePackageResponse.builder()
                .cigarettePackageId(cigarettePackage.getCigaretteId())
                .cigarettePackageName(cigarettePackage.getCigaretteName())
                .brand(cigarettePackage.getBrand())
                .flavor(cigarettePackage.getFlavor())
                .nicotineLevel(cigarettePackage.getNicoteneStrength())
                .sticksPerPack(cigarettePackage.getSticksPerPack())
                .pricePerPack(cigarettePackage.getPrice())
                .build();
    }


}
