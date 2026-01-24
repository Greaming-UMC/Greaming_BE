package com.umc.greaming.domain.challenge.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.challenge.enums.Cycle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "challenges",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_challenge_cycle_period",
                        columnNames = {"cycle", "period_key"}
                )
        }
)

public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Cycle cycle;

    @Column(name = "period_key", nullable = false, length = 20)
    private String periodKey;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer participant = 0;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "reference_content", columnDefinition = "TEXT")
    private String referenceContent;

    @Column(name = "is_archived", nullable = false)
    @ColumnDefault("false")
    private Boolean isArchived = false;

}
