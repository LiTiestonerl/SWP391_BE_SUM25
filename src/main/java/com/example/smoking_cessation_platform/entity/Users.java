package com.example.smoking_cessation_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"role", "userMemberPackages", "smokingStatuses", "quitPlansAsUser", "quitPlansAsCoach",
        "userBadges", "notifications", "posts", "comments", "chatSessionsAsUser",
        "chatSessionsAsCoach", "messagesSent", "ratingsAsMember", "ratingsAsCoach",
        "paymentTransactions", "emailVerificationTokens"})
@SuperBuilder
@NoArgsConstructor
@Table(name = "users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_public_id", unique = true)
    private String userPublicId;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "status")
    private String status = "active";

    @Column(name = "is_2fa_enabled", nullable = false)
    private Boolean is2FaEnabled = false;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EmailVerificationToken> emailVerificationTokens = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserMemberPackage> userMemberPackages = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SmokingStatus> smokingStatuses = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<QuitPlan> quitPlansAsUser = new HashSet<>();

    @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY)
    private Set<QuitPlan> quitPlansAsCoach = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserBadge> userBadges = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatSession> chatSessionsAsUser = new HashSet<>();

    @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY)
    private Set<ChatSession> chatSessionsAsCoach = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatMessage> messagesSent = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Rating> ratingsAsMember = new HashSet<>();

    @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY)
    private Set<Rating> ratingsAsCoach = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PaymentTransaction> paymentTransactions = new HashSet<>();

    public void addEmailVerificationToken(EmailVerificationToken token) {
        this.emailVerificationTokens.add(token);
        token.setUser(this);
    }

    public void removeEmailVerificationToken(EmailVerificationToken token) {
        this.emailVerificationTokens.remove(token);
        token.setUser(null);
    }
}
