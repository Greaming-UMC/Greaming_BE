package com.umc.greaming.domain.challenge.dto.response;

import com.umc.greaming.common.response.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "챌린지 게시물 목록 응답")
public record ChallengeSubmissionsResponse(
        @Schema(description = "게시물 카드 목록")
        List<ChallengeSubmissionCard> submissions,

        @Schema(description = "페이지네이션 정보")
        PageInfo pageInfo
) {
    public static ChallengeSubmissionsResponse from(Page<?> page, List<ChallengeSubmissionCard> submissions) {
        return new ChallengeSubmissionsResponse(
                submissions,
                PageInfo.from(page)
        );
    }
}
