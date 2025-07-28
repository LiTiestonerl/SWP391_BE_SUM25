package com.example.smoking_cessation_platform.dto.smokingStatus;

import com.example.smoking_cessation_platform.Enum.NicotineStrength;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmokingStatusRequest {

    @NotNull(message = "Số điếu thuốc không được để trống")
    @PositiveOrZero(message = "Số điếu thuốc phải là số không âm")
    private Integer cigarettesPerDay;

    @NotNull(message = "Tần suất không được để trống")
    @Pattern(regexp = "^(DAILY|OCCASIONALLY|SOCIAL|STRESS)$",
            message = "Tần suất phải là một trong: DAILY, OCCASIONALLY, SOCIAL, STRESS")
    private String frequency;

    @NotNull(message = "Hương vị ưa thích không được để trống")
    @Pattern(regexp = "^(MENTHOL|VANILLA|CHERRY|CHOCOLATE|ORIGINAL|MINT)$",
            message = "Hương vị phải là một trong: MENTHOL, VANILLA, CHERRY, CHOCOLATE, ORIGINAL, MINT")
    private String preferredFlavor;

    @NotNull(message = "Mức nicotine ưa thích không được để trống")
    private NicotineStrength preferredNicotineLevel;

    private Long packageId;

    @NotNull(message = "Ngày ghi nhận không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

}
