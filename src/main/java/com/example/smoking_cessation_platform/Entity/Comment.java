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
@Table(name = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;

    @Column(name = "status")
    private String status = "active";

    @Column(name = "content")
    private String content;

}
