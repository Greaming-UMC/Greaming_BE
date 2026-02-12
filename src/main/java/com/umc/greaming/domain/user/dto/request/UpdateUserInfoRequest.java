package com.umc.greaming.domain.user.dto.request;

import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "유저 정보 수정 요청 (모든 필드 nullable — null이면 변경 없음)")
public record UpdateUserInfoRequest(
        @Schema(description = "닉네임", example = "그림쟁이")
        String nickname,

        @Schema(description = "자기소개", example = "그림 그리는 것을 좋아합니다.")
        String intro,

        @Schema(description = "전문 분야 태그 목록 (null이면 변경 없음, 보내면 전체 교체)", example = "[\"일러스트\", \"캐릭터\"]")
        List<String> specialtyTags,

        @Schema(description = "관심 분야 태그 목록 (null이면 변경 없음, 보내면 전체 교체)", example = "[\"풍경\", \"인물\"]")
        List<String> interestTags,

        @Schema(description = "Journey 레벨", example = "PAINTER")
        JourneyLevel journeyLevel,

        @Schema(description = "주간 목표 점수", example = "5")
        Integer weeklyGoalScore,

        @Schema(description = "프로필 이미지 S3 key (null이면 변경 없음, presigned URL 발급 후 업로드한 key)", example = "profile/abc123.jpg")
        String profileImageKey
) {}
