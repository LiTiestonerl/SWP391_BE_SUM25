package com.example.smoking_cessation_platform.dto.memberpackage;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPackageRequest {
    @NotBlank(message = "Tên gói không được để trống")
    @Size(max = 255, message = "Tên gói không được vượt quá 255 ký tự")
    private String packageName;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải là một giá trị hợp lệ")
    private BigDecimal price;

    @NotNull(message = "Thời lượng không được để trống")
    @Positive(message = "Thời lượng phải là số dương")
    private Integer duration;

    @Size(max = 65535, message = "Mô tả tính năng quá dài")
    private String featuresDescription;
}
