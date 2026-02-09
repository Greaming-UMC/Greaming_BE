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
        // null이 아니거나 빈 문자열이 아닐 때만 업데이트 (정책에 따라 조정 가능)
        if (title != null) {
            this.title = title;
        }
        if (caption != null) {
            this.caption = caption;
        }
    }

    // 2. 공개 범위 변경 (Service에서 String으로 넘길 경우를 대비해 오버로딩 or 타입 일치 필요)
    // 여기서는 Service가 DTO(String) -> Enum 변환을 해서 넘겨준다고 가정하고 Enum으로 받습니다.
    public void updateVisibility(String visibilityStr) {
        if (visibilityStr != null) {
            try {
                this.visibility = SubmissionVisibility.valueOf(visibilityStr);
            } catch (IllegalArgumentException e) {
                // 잘못된 Enum 값이 들어오면 무시하거나 예외 처리 (여기선 기존 유지)
            }
        }
    }

    // Service에서 이미 Enum으로 변환해서 준다면 이 메서드 사용
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

    // 5. 조회수/좋아요 등 카운트 메서드
    public void increaseLikeCount() { this.likeCount++; }
    public void decreaseLikeCount() { if (this.likeCount > 0) this.likeCount--; }

    public void increaseCommentCount() { this.commentCount++; }
    public void decreaseCommentCount() { if (this.commentCount > 0) this.commentCount--; }

    // [수정] 아래 메서드에서 컴파일 에러가 났었습니다. (thumbnailUrl -> thumbnailKey)
    public void update(String title, String caption, SubmissionVisibility visibility, Boolean commentEnabled, String thumbnailKey) {
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
        if (thumbnailKey != null && !thumbnailKey.isBlank()) {
            this.thumbnailKey = thumbnailKey; // [수정됨] this.thumbnailUrl (X) -> this.thumbnailKey (O)
        }
    }
}