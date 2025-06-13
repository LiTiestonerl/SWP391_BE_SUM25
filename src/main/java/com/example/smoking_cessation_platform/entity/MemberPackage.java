package com.example.smoking_cessation_platform.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "member_package")
public class MemberPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_package_id", nullable = false)
    private Integer memberPackageId;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "price")
    private BigDecimal price;

    /**
     * Tháng
     */
    @Column(name = "duration")
    @Schema(description = "Tháng")
    private Integer duration;

    @Column(name = "features_description")
    private String featuresDescription;

}
