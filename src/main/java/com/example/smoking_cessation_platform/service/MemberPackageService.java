package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.entity.MemberPackage;
import com.example.smoking_cessation_platform.dto.memberPackage.MemberPackageRequest;
import com.example.smoking_cessation_platform.dto.memberPackage.MemberPackageResponse;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.UserMemberPackage;
import com.example.smoking_cessation_platform.repository.MemberPackageRepository;
import com.example.smoking_cessation_platform.repository.UserMemberPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberPackageService {

    @Autowired
    private MemberPackageRepository memberPackageRepository;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

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

    @Transactional
   public void grantMemberPackage(User user, MemberPackage memberPackage, User coach) {

        checkCoachSupported(memberPackage, coach);
        // Lấy danh sách gói đang active
        List<UserMemberPackage> activePackages = userMemberPackageRepository.findByUser_UserIdAndStatus(user.getUserId(), "active");

        for (UserMemberPackage ump : activePackages) {
            BigDecimal currentPrice = ump.getMemberPackage().getPrice();
            BigDecimal newPrice = memberPackage.getPrice();

            // Nếu gói hiện tại đang active có giá cao hơn gói mới => chặn downgrade
            if (currentPrice.compareTo(newPrice) > 0) {
                throw new IllegalStateException("Bạn đang sở hữu gói có giá cao hơn, không thể mua gói thấp hơn.");
            }
        }

        // Vô hiệu hoá tất cả gói active hiện tại
        userMemberPackageRepository.deactivateAllByUser(user.getUserId());

        // Giới hạn duration tối đa (ví dụ: 120 tháng)
        int maxDurationMonths = 1000;
        int duration = memberPackage.getDuration();
        if (duration > maxDurationMonths) {
            throw new IllegalArgumentException("Thời hạn gói vượt quá giới hạn cho phép (" + maxDurationMonths + " tháng)");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(duration);

        // Kiểm tra ngày vượt quá giới hạn MySQL (9999-12-31)
        if (endDate.isAfter(LocalDate.of(9999, 12, 31))) {
            throw new IllegalArgumentException("Ngày kết thúc vượt quá giới hạn cho phép trong cơ sở dữ liệu");
        }

        UserMemberPackage userMemberPackage = new UserMemberPackage();
        userMemberPackage.setUser(user);
        userMemberPackage.setMemberPackage(memberPackage);
        userMemberPackage.setStartDate(startDate);
        userMemberPackage.setEndDate(endDate);
        userMemberPackage.setStatus("active");

        if (coach != null) {
            userMemberPackage.setCoach(coach); // giả sử UserMemberPackage có trường coach
        }
        userMemberPackageRepository.save(userMemberPackage);
    }

    private void checkCoachSupported(MemberPackage memberPackage, User coach) {
        if (coach != null && !memberPackage.getSupportedCoaches().contains(coach)) {
            throw new IllegalStateException("Coach này không được hỗ trợ bởi gói thành viên bạn đang mua.");
        }
    }


}
