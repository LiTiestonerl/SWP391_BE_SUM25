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
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "userMemberPackages")
@SuperBuilder
@NoArgsConstructor
@Table(name = "member_package")
public class MemberPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_package_id", nullable = false)
    private Integer memberPackageId;

    @Schema(description = "Tên gói membership", example = "Gói VIP 3 tháng")
    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Schema(description = "Giá của gói (VND)", example = "150000")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Tháng
     */
    @Schema(description = "Thời gian hiệu lực của gói (tính theo tháng)", example = "3")
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Schema(description = "Mô tả chi tiết tính năng của gói", example = "Hỗ trợ tư vấn riêng, ưu đãi giảm giá")
    @Column(name = "features_description", columnDefinition = "TEXT")
    private String featuresDescription;

    @ManyToMany
    @JoinTable(
            name = "member_package_supported_coach",
            joinColumns = @JoinColumn(name = "member_package_id"),
            inverseJoinColumns = @JoinColumn(name = "coach_id")
    )
    private Set<User> supportedCoaches = new HashSet<>();

    @OneToMany(mappedBy = "memberPackage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserMemberPackage> userMemberPackages = new HashSet<>();

    public void addUserMemberPackage(UserMemberPackage userMemberPackage) {
        this.userMemberPackages.add(userMemberPackage);
        userMemberPackage.setMemberPackage(this);
    }

    public void removeUserMemberPackage(UserMemberPackage userMemberPackage) {
        this.userMemberPackages.remove(userMemberPackage);
        userMemberPackage.setMemberPackage(null);
    }
}