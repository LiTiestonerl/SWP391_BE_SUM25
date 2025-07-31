package com.example.smoking_cessation_platform.dto.quitplan;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlanResponse {
    private Integer planId;
    private String title;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private QuitPlanStatus status;
    private String reason;
    private String stagesDescription;
    private String customNotes;
    private Integer cigarettesPerDay;

    private Long userId;   // ID người dùng
    private Long coachId;  // ID huấn luyện viên
    private Integer recommendedPackageId;  // ID gói thuốc lá khuyến nghị

    private Set<QuitPlanStageResponse> quitPlanStages;  // Danh sách giai đoạn
}
