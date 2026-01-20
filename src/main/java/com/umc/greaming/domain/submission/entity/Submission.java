package com.umc.greaming.domain.submission.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.submission.enums.SubmissionField;
import com.umc.greaming.domain.submission.enums.Visibility;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    private Challenge challenge; // Nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id")
    private Circle circle; // Nullable
*/
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false)
    private SubmissionField field; // 게시물 유형 WEEKLY, DAILY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @ColumnDefault("'PUBLIC'")
    private Visibility visibility; // 공개범위 (PUBLIC, CIRCLE)

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
}
