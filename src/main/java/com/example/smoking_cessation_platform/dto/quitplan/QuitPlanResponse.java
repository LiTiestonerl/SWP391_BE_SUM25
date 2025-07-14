package com.example.smoking_cessation_platform.dto.quitplan;

import com.example.smoking_cessation_platform.Enum.QuitPlanStatus;
import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
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
    private Long userId; // ID của người dùng
    private Long coachId; // ID của huấn luyện viên
    private Integer recommendedPackageId; // ID của gói thuốc lá
    private Set<QuitPlanStageResponse> quitPlanStages; // Danh sách các giai đoạn
    private List<CigarettePackageDTO> nicotineSuggestions;
}
