package com.example.smoking_cessation_platform.dto.CigarettePackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CigarettePackageDTO {
    private Long cigaretteId;             // Cho response: khi trả về dữ liệu đã có ID
    private String cigaretteName;
    private BigDecimal price;
    private String brand;
    private String nicoteneStrength;
    private String flavor;
    private Integer sticksPerPack;
    private Double nicotineMg;
}
