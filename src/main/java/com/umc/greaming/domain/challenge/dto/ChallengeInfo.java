package com.umc.greaming.domain.challenge.dto;

import com.umc.greaming.domain.challenge.entity.Challenge;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ChallengeInfo(
        @Schema(description = "챌린지 ID", example = "1")
        Long challengeId,

        @Schema(description = "챌린지 제목", example = "2월 2주차 주간 챌린지")
        String title,

        @Schema(description = "챌린지 설명", example = "이번 주제는 '겨울'입니다.")
        String description,

        @Schema(description = "참고 자료/링크", example = "https://example.com/ref")
        String referenceContent,

        @Schema(description = "챌린지 주기 (WEEKLY, DAILY)", example = "WEEKLY")
        String cycle,

        @Schema(description = "기간 키 (식별자)", example = "2024-W02")
        String periodKey,

        @Schema(description = "시작일 (yyyy-MM-dd)", example = "2024-02-10")
        String startAt,

        @Schema(description = "종료일 (yyyy-MM-dd)", example = "2024-02-17")
        String endAt,

        @Schema(description = "참여자 수", example = "15")
        Integer participant,

        @Schema(description = "진행 상태 (WAITING, PROCEEDING, END)", example = "PROCEEDING")
        String status,

        @Schema(description = "아카이브 여부", example = "false")
        Boolean isArchived
) {
    public static ChallengeInfo from(Challenge challenge) {
        if (challenge == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String currentStatus = "WAITING";

        if (now.isAfter(challenge.getStartAt()) && now.isBefore(challenge.getEndAt())) {
            currentStatus = "PROCEEDING";
        } else if (now.isAfter(challenge.getEndAt())) {
            currentStatus = "END";
        }

        return new ChallengeInfo(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getReferenceContent(),
                challenge.getCycle().name(),
                challenge.getPeriodKey(),
                challenge.getStartAt().format(formatter),
                challenge.getEndAt().format(formatter),
                challenge.getParticipant(),
                currentStatus,
                challenge.getIsArchived()
        );
    }
}