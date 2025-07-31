package com.example.smoking_cessation_platform.entity;

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

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Tháng
     */
    @Column(name = "duration", nullable = false)
    private Integer duration;

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