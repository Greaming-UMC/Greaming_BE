package com.umc.greaming.domain.comment.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Column(name ="content",nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() { this.likeCount++; }
    public void decreaseLikeCount() { if (this.likeCount > 0) this.likeCount--; }
}
