package com.example.smoking_cessation_platform.dto.smokingstatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmokingStatusRequest {

    @NotNull(message = "Số điếu thuốc không được để trống")
    @PositiveOrZero(message = "Số điếu thuốc phải là số không âm")
    private Integer cigarettesPerDay;

    @Size(max = 255, message = "Tần suất không được vượt quá 255 ký tự")
    private String frequency;

    private Long packageId;

    @NotNull(message = "Giá mỗi gói không được để trống")
    @PositiveOrZero(message = "Giá mỗi gói phải là số không âm")
    private BigDecimal pricePerPack;

    @NotNull(message = "Ngày ghi nhận không được để trống")
    private LocalDate recordDate;

}
