package com.umc.greaming.domain.submission.entity;

import com.umc.greaming.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission_images",
        uniqueConstraints = {
            @UniqueConstraint(
                name = "uk_submission_id_sort_order",
                columnNames = {"submission_id", "sort_order"}
            )
        })
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer sortOrder;
}
