package com.umc.greaming.domain.user.dto.request;

import com.umc.greaming.domain.user.entity.enums.ArtField;
import com.umc.greaming.domain.user.entity.enums.ArtStyle;
import com.umc.greaming.domain.user.entity.enums.UsagePurpose;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "최초 유저 정보 등록 요청")
public record RegistInfoRequest(
        @Schema(description = "닉네임", example = "그림쟁이")
        String nickname,

        @Schema(description = "자기소개", example = "그림 그리는 것을 좋아합니다.")
        String intro,

        @Schema(description = "전문 분야 정보")
        SpecialtyInfo specialties,

        @Schema(description = "관심 분야 정보")
        InterestInfo interests,

        @Schema(description = "사용 목적", example = "PAINTER")
        UsagePurpose usagePurpose,

        @Schema(description = "주간 목표 점수", example = "5")
        Integer weeklyGoalScore
) {

    @Schema(description = "전문 분야 상세 정보")
    public record SpecialtyInfo(
            @Schema(description = "전문 분야 목록", example = "[\"ILLUSTRATION\", \"CHARACTER\"]")
            List<ArtField> fields,

            @Schema(description = "전문 스타일", example = "DIGITAL")
            ArtStyle style
    ) {}

    @Schema(description = "관심 분야 상세 정보")
    public record InterestInfo(
            @Schema(description = "관심 분야 목록", example = "[\"LANDSCAPE\", \"PORTRAIT\"]")
            List<ArtField> fields,

            @Schema(description = "관심 스타일", example = "WATERCOLOR")
            ArtStyle style
    ) {}
}