package com.example.smoking_cessation_platform.dto.cigarettePackage;

import com.example.smoking_cessation_platform.enums.NicotineStrength;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CigarettePackagerequest {

    @NotNull(message = "Tên thuốc lá không được để trống")
    private String cigaretteName;

    @NotNull(message = "Thương hiệu thuốc lá không được để trống")
    private String cigaretteBrand;

    @NotNull(message = "Giá thuốc lá không được để trống")
    private BigDecimal price;

    @NotNull(message = "Hương vị không được để trống")
    @Pattern(regexp = "^(MENTHOL|VANILLA|CHERRY|CHOCOLATE|ORIGINAL|MINT)$",
            message = "Hương vị phải là một trong: MENTHOL, VANILLA, CHERRY, CHOCOLATE, ORIGINAL, MINT")
    private String flavor;

    @NotNull(message = "Cường độ nicotine không được để trống")
    private NicotineStrength nicoteneStrength;

    @NotNull(message = "Số điếu thuốc trong mỗi gói không được để trống")
    private Integer sticksPerPack;


}
