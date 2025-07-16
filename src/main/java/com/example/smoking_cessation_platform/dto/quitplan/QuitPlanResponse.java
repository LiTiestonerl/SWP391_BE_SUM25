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

    private Long userId;   // ID người dùng
    private Long coachId;  // ID huấn luyện viên
    private Integer recommendedPackageId;  // ID gói thuốc lá khuyến nghị

    private Set<QuitPlanStageResponse> quitPlanStages;  // Danh sách giai đoạn
    private List<CigarettePackageDTO> nicotineSuggestions;  // Danh sách gợi ý nicotine

    // 🔽 Các trường mở rộng để hiển thị lịch sử gợi ý (nếu có)
    private Long fromPackageId;        // Từ gói nào
    private Long toPackageId;          // Gợi ý sang gói nào
    private String recommendationNotes; // Ghi chú (nếu hệ thống tự động gợi ý)
}
