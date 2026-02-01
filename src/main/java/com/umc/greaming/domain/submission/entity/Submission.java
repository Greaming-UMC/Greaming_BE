package com.umc.greaming.domain.submission.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.submission.enums.SubmissionField;
import com.umc.greaming.domain.submission.enums.SubmissionVisibility;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Submission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id")
    private Circle circle;
*/

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "caption", columnDefinition = "TEXT")
    private String caption;

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false)
    private SubmissionField field;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    @ColumnDefault("'PUBLIC'")
    private SubmissionVisibility visibility;

    @Column(name = "comment_enabled", nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private boolean commentEnabled = true;

    @Column(name = "like_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default
    private int likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default
    private int commentCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default
    private int bookmarkCount = 0;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}