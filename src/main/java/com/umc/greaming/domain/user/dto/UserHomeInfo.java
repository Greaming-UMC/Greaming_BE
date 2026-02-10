package com.umc.greaming.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.circle.entity.Circle;
import com.umc.greaming.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 화면용 사용자 요약 정보")
public record UserHomeInfo(
        @Schema(description = "사용자 ID", example = "1")
        @JsonProperty("userId")
        Long userId,

        @Schema(description = "사용자 닉네임", example = "그림쟁이")
        @JsonProperty("nickname")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://s3.../profile.jpg")
        @JsonProperty("profileImageUrl")
        String profileImageUrl,

        @Schema(description = "사용자 레벨", example = "SKETCHER")
        @JsonProperty("level")
        String level,

        @Schema(description = "가입한 서클 ID (없으면 null)", example = "5")
        @JsonProperty("circleId")
        Long circleId,

        @Schema(description = "서클 이름 (없으면 null)", example = "그림 동아리")
        @JsonProperty("circleName")
        String circleName,

        @Schema(description = "서클 프로필 이미지 URL (없으면 null)", example = "https://s3.../circle.jpg")
        @JsonProperty("circleProfileUrl")
        String circleProfileUrl
) {
    public static UserHomeInfo of(User user, String profileUrl, String level, Circle circle, String circleProfileUrl) {
        return new UserHomeInfo(
                user.getUserId(),
                user.getNickname(),
                profileUrl,
                level,
                circle != null ? circle.getId() : null,
                circle != null ? circle.getName() : null,
                circleProfileUrl
        );
    }
}
