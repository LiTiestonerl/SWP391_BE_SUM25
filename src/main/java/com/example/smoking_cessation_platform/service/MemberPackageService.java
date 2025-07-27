package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.MemberPackage;
import com.example.smoking_cessation_platform.dto.memberPackage.MemberPackageRequest;
import com.example.smoking_cessation_platform.dto.memberPackage.MemberPackageResponse;
import com.example.smoking_cessation_platform.repository.MemberPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberPackageService {

    @Autowired
    private MemberPackageRepository memberPackageRepository;

    /**
     * Tạo một gói đăng ký mới.
     * @param createDto DTO chứa thông tin gói cần tạo.
     * @return DTO phản hồi của gói đã được tạo.
     */
    @Transactional
    public MemberPackageResponse createMemberPackage(MemberPackageRequest createDto) {
        MemberPackage memberPackage = MemberPackage.builder()
                .packageName(createDto.getPackageName())
                .price(createDto.getPrice())
                .duration(createDto.getDuration())
                .featuresDescription(createDto.getFeaturesDescription())
                .build();

        MemberPackage savedPackage = memberPackageRepository.save(memberPackage);

        return convertToDto(savedPackage);
    }

    /**
     * Lấy tất cả các gói đăng ký.
     * @return Danh sách các DTO phản hồi của gói đăng ký.
     */
    public List<MemberPackageResponse> getAllMemberPackages() {
        List<MemberPackage> memberPackages = memberPackageRepository.findAll();
        return memberPackages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một gói đăng ký theo ID.
     * @param id ID của gói đăng ký.
     * @return Optional chứa DTO phản hồi của gói (nếu tìm thấy).
     */
    public Optional<MemberPackageResponse> getMemberPackageById(Integer id) {
        return memberPackageRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Cập nhật thông tin một gói đăng ký.
     * @param id ID của gói đăng ký cần cập nhật.
     * @param updateDto DTO chứa thông tin cập nhật.
     * @return Optional chứa DTO phản hồi của gói đã cập nhật (nếu tìm thấy).
     */
    @Transactional
    public Optional<MemberPackageResponse> updateMemberPackage(Integer id, MemberPackageRequest updateDto) {
        return memberPackageRepository.findById(id)
                .map(existingPackage -> {
                    existingPackage.setPackageName(updateDto.getPackageName());
                    existingPackage.setPrice(updateDto.getPrice());
                    existingPackage.setDuration(updateDto.getDuration());
                    existingPackage.setFeaturesDescription(updateDto.getFeaturesDescription());

                    MemberPackage updatedPackage = memberPackageRepository.save(existingPackage);
                    return convertToDto(updatedPackage);
                });
    }

    /**
     * Xóa một gói đăng ký theo ID.
     * @param id ID của gói đăng ký cần xóa.
     * @return true nếu xóa thành công, false nếu không tìm thấy gói.
     */
    @Transactional
    public boolean deleteMemberPackage(Integer id) {
        if (memberPackageRepository.existsById(id)) {
            memberPackageRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Phương thức hỗ trợ chuyển đổi từ Entity MemberPackage sang MemberPackageResponseDTO.
     * @param memberPackage Entity MemberPackage.
     * @return MemberPackageResponseDTO.
     */
    private MemberPackageResponse convertToDto(MemberPackage memberPackage) {
        return MemberPackageResponse.builder()
                .memberPackageId(memberPackage.getMemberPackageId())
                .packageName(memberPackage.getPackageName())
                .price(memberPackage.getPrice())
                .duration(memberPackage.getDuration())
                .featuresDescription(memberPackage.getFeaturesDescription())
                .build();
    }
}
