package com.umc.greaming.domain.calendar.entity;

import com.umc.greaming.domain.calendar.enums.ChallengeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "challenge_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type", nullable = false, length = 20)
    private ChallengeType challengeType;

    @Column(name = "participated_at", nullable = false)
    private LocalDate participatedAt;
}
