package com.umc.greaming.domain.submission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 좋아요 응답 DTO")
public record SubmissionLikeResponse(
        @Schema(description = "현재 좋아요 상태 (true: 좋아요 중, false: 좋아요 취소됨)", example = "true")
        boolean isLiked,

        @Schema(description = "현재 총 좋아요 수", example = "42")
        int likeCount
) {
    public static SubmissionLikeResponse of(boolean isLiked, int likeCount) {
        return new SubmissionLikeResponse(isLiked, likeCount);
    }
}
