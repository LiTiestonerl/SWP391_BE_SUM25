package com.example.smoking_cessation_platform.dto.userbadge;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserBadgeRequest {
    private Long  userId;           // ID của người dùng nhận huy hiệu
    private Integer badgeId;          // ID của huy hiệu
    private LocalDate dateAchieved;   // Ngày đạt được huy hiệu
    private Boolean shared;           // Có chia sẻ huy hiệu hay không (nếu bạn có dùng field shared)
}