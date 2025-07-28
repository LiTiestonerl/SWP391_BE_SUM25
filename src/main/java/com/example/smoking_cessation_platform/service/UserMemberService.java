package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.usermemberpackage.UserMemberPackageResponse;
import com.example.smoking_cessation_platform.entity.MemberPackage;
import com.example.smoking_cessation_platform.entity.User;
import com.example.smoking_cessation_platform.entity.UserMemberPackage;
import com.example.smoking_cessation_platform.repository.MemberPackageRepository;
import com.example.smoking_cessation_platform.repository.UserMemberPackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserMemberService {
    @Autowired
    private MemberPackageRepository memberPackageRepository;

    @Autowired
    private UserMemberPackageRepository userMemberPackageRepository;

    @Transactional
    public void assignFreePackageToUser(User user ) {
        // Tìm gói FREE bằng ID cố định
        MemberPackage freePackage = memberPackageRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói FREE"));

        UserMemberPackage ump = new UserMemberPackage();
        ump.setUser(user);
        ump.setMemberPackage(freePackage);
        ump.setStartDate(LocalDate.now());
        ump.setStatus("active"); // hoặc enum tuỳ bạn
        userMemberPackageRepository.save(ump);
    }

    private UserMemberPackageResponse toResponse(UserMemberPackage ump) {
        return UserMemberPackageResponse.builder()
                .userMemberPackageId(ump.getUserMemberPackageId())
                .startDate(ump.getStartDate())
                .endDate(ump.getEndDate())
                .status(ump.getStatus())
                .memberPackageId(ump.getMemberPackage().getMemberPackageId())
                .build();
    }

    public UserMemberPackageResponse getCurrentPackage(Long userId) {
        // Tìm gói hiện tại của user
        Optional<UserMemberPackage> optional = userMemberPackageRepository
                .findFirstByUser_UserIdAndStatusOrderByStartDateDesc(userId, "active");

        UserMemberPackage ump = optional.orElseThrow(() ->
                new RuntimeException("Người dùng chưa có gói nào đang active"));

        return toResponse(ump);
    }

    @Transactional
    public void cancelCurrentPackage(Long userId) {
        // Tìm gói đang active hiện tại
        UserMemberPackage currentPackage = userMemberPackageRepository
                .findFirstByUser_UserIdAndStatusOrderByStartDateDesc(userId, "active")
                .orElseThrow(() -> new RuntimeException("Người dùng chưa có gói active nào để hủy."));

        // Kiểm tra nếu gói hiện tại là FREE (ví dụ FREE có ID = 1)
        if (currentPackage.getMemberPackage().getMemberPackageId() == 1) {
            throw new RuntimeException("Gói FREE mặc định không thể hủy.");
        }

        // Nếu không phải FREE thì hủy bình thường
        currentPackage.setStatus("inactive");
        userMemberPackageRepository.save(currentPackage);
    }
}
