package com.umc.greaming.domain.submission.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.challenge.entity.Challenge;
import com.umc.greaming.domain.circle.entity.Circle;
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
// @Setter [제거] : 엔티티의 일관성을 위해 닫음
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id")
    private Circle circle;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "thumbnail_key", nullable = false)
    private String thumbnailKey;

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

    // --- [비즈니스 로직 메서드] ---

    // 1. 게시글 정보 수정 (제목, 설명)
    public void updateInfo(String title, String caption) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (caption != null) {
            this.caption = caption;
        }
    }

    // 2. 공개 범위 변경
    public void updateVisibility(SubmissionVisibility visibility) {
        if (visibility != null) {
            this.visibility = visibility;
        }
    }

    // 3. 댓글 허용 여부 변경
    public void changeCommentEnabled(Boolean commentEnabled) {
        if (commentEnabled != null) {
            this.commentEnabled = commentEnabled;
        }
    }

    // 4. 삭제 처리 (Soft Delete)
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 5. 좋아요/댓글/북마크 수 조정
    public void increaseLikeCount() { this.likeCount++; }
    public void decreaseLikeCount() { if (this.likeCount > 0) this.likeCount--; }

    public void increaseCommentCount() { this.commentCount++; }
    public void decreaseCommentCount() { if (this.commentCount > 0) this.commentCount--; }

    public void update(String title, String caption, SubmissionVisibility visibility, Boolean commentEnabled, String thumbnailUrl) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (caption != null) {
            this.caption = caption;
        }
        if (visibility != null) {
            this.visibility = visibility;
        }
        if (commentEnabled != null) {
            this.commentEnabled = commentEnabled;
        }
        if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }
}