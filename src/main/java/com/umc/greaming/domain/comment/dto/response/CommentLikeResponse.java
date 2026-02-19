package com.umc.greaming.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 좋아요 응답 DTO")
public record CommentLikeResponse(
        @Schema(description = "현재 좋아요 상태 (true: 좋아요 중, false: 좋아요 취소됨)", example = "true")
        boolean isLiked,

        @Schema(description = "현재 총 좋아요 수", example = "5")
        int likeCount
) {
    public static CommentLikeResponse of(boolean isLiked, int likeCount) {
        return new CommentLikeResponse(isLiked, likeCount);
    }
}
