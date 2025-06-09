package com.example.smoking_cessation_platform.Entity;

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
@Table(name = "cigarette_package")
public class CigarettePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cigarette_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cigaretteId;

    @Column(name = "cigarette_name", nullable = false)
    private String cigaretteName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "sticks_per_pack", nullable = false)
    private Integer sticksPerPack;

}
