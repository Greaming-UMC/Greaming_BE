package com.umc.greaming.domain.home.dto.response;

import com.umc.greaming.common.response.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "게시물 목록 응답")
public record HomeSubmissionsResponse(
        @Schema(description = "게시물 카드 목록")
        List<HomeSubmissionCard> submissions,

        @Schema(description = "페이지네이션 정보")
        PageInfo pageInfo
) {
    public static HomeSubmissionsResponse from(Page<?> page, List<HomeSubmissionCard> submissions) {
        return new HomeSubmissionsResponse(
                submissions,
                PageInfo.from(page)
        );
    }
}
