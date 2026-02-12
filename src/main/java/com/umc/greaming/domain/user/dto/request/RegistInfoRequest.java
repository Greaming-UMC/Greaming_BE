package com.umc.greaming.domain.user.dto.request;

import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "최초 유저 정보 등록 요청")
public record RegistInfoRequest(
        @Schema(description = "닉네임", example = "그림쟁이")
        String nickname,

        @Schema(description = "자기소개", example = "그림 그리는 것을 좋아합니다.")
        String intro,

        @Schema(description = "전문 분야 태그 목록", example = "[\"일러스트\", \"캐릭터\"]")
        List<String> specialtyTags,

        @Schema(description = "관심 분야 태그 목록", example = "[\"풍경\", \"인물\"]")
        List<String> interestTags,

        @Schema(description = "Journey 레벨", example = "PAINTER")
        JourneyLevel journeyLevel,

        @Schema(description = "주간 목표 점수", example = "5")
        Integer weeklyGoalScore
) {}
