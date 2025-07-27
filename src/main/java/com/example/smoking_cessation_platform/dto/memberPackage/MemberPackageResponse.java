package com.example.smoking_cessation_platform.dto.memberPackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPackageResponse {

    private Integer memberPackageId;

    private String packageName;

    private BigDecimal price;

    private Integer duration;

    private String featuresDescription;
}
