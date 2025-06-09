package com.example.smoking_cessation_platform.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Integer notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "status")
    private String status = "sent";

    @Column(name = "quit_plan_id")
    private Integer quitPlanId;

    @Column(name = "achievement_id")
    private Integer achievementId;

    @Column(name = "content")
    private String content;

}
