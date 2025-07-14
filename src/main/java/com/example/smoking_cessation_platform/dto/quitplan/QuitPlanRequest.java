package com.example.smoking_cessation_platform.dto.quitplan;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private QuitPlanStatus status;
    private String reason;
    private String stagesDescription;
    private String customNotes;
    private Long userId; // ID của người dùng
    private Long coachId; // ID của huấn luyện viên
    private Integer recommendedPackageId; // ID của gói thuốc lá
}
