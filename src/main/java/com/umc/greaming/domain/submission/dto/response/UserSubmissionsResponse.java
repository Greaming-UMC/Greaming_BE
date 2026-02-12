package com.umc.greaming.domain.submission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "유저별 게시글 리스트 응답 DTO")
public record UserSubmissionsResponse(
        @Schema(description = "게시글 카드 리스트")
        List<UserSubmissionCard> submissions,

        @Schema(description = "페이지 정보")
        PageInfo pageInfo
) {
    @Schema(description = "페이지 정보")
    public record PageInfo(
            @Schema(description = "현재 페이지 번호", example = "1")
            int currentPage,

            @Schema(description = "페이지 크기", example = "20")
            int pageSize,

            @Schema(description = "전체 페이지 수", example = "5")
            int totalPages,

            @Schema(description = "전체 게시글 수", example = "100")
            long totalElements,

            @Schema(description = "마지막 페이지 여부", example = "false")
            boolean isLast,

            @Schema(description = "첫 페이지 여부", example = "true")
            boolean isFirst
    ) {
    }

    public static UserSubmissionsResponse of(List<UserSubmissionCard> submissions, PageInfo pageInfo) {
        return new UserSubmissionsResponse(submissions, pageInfo);
    }
}
