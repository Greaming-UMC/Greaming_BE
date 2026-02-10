package com.umc.greaming.domain.home.dto.response;

import com.umc.greaming.domain.challenge.dto.ChallengeInfo;
import com.umc.greaming.domain.user.dto.UserHomeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

public record HomeResponse(
        @Schema(description = "데일리 챌린지 정보")
        ChallengeInfo dailyChallengeInfo,

        @Schema(description = "주간 챌린지 정보")
        ChallengeInfo weeklyChallengeInfo,

        @Schema(description = "사용자 요약 정보 (로그인 안했으면 null)")
        UserHomeInfo userHomeInfo
) {
    public static HomeResponse from(ChallengeInfo dailyChallengeInfo, ChallengeInfo weeklyChallengeInfo, UserHomeInfo userHomeInfo) {
        return new HomeResponse(dailyChallengeInfo, weeklyChallengeInfo, userHomeInfo);
    }
}