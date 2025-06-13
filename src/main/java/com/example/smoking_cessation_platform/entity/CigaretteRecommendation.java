package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "cigarette_recommendation")
public class CigaretteRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rec_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recId;

    @Column(name = "from_package_id", nullable = false)
    private Long fromPackageId;

    @Column(name = "to_package_id", nullable = false)
    private Long toPackageId;

    @Column(name = "notes")
    private String notes;

}
