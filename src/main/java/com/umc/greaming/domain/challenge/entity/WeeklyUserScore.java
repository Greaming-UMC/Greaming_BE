package com.umc.greaming.domain.challenge.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weekly_user_scores")
public class WeeklyUserScore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_user_score_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "goal_score")
    private Integer goalScore;

    @Column(name = "base_score", nullable = false)
    @ColumnDefault("0")
    private Integer baseScore = 0;

    @Column(name = "attendance_score", nullable = false)
    @ColumnDefault("0")
    private Integer attendanceScore = 0;

    @Column(name = "like_score", nullable = false)
    @ColumnDefault("0")
    private Integer likeScore = 0;

    @Column(name = "goal_bonus", nullable = false)
    @ColumnDefault("0")
    private Integer goalBonus = 0;

    @Column(name = "total_score", nullable = false)
    @ColumnDefault("0")
    private Integer totalScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "journey_level", length = 10)
    private JourneyLevel journeyLevel;

    @Column(name = "rank_in_level")
    private Integer rankInLevel;

}