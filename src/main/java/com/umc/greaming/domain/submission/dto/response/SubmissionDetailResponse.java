package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 상세 조회 응답 DTO")
public record SubmissionDetailResponse(
        @Schema(description = "게시글 핵심 정보 (작성자, 이미지, 내용 등)")
        SubmissionInfo submission,
        @Schema(description = "내가 작성한 게시물인지 여부")
        Boolean isWriter,

        @Schema(description = "게시글 필드 (WEEKLY, DAILY, FREE)", example = "WEEKLY")
        String field,

        @Schema(description = "챌린지 ID (FREE인 경우 null)", example = "1")
        Long challengeId,

        @Schema(description = "챌린지 제목 (FREE인 경우 null)", example = "주간 챌린지")
        String challengeTitle,

        @Schema(description = "댓글 페이징 데이터")
        CommentPageResponse commentPage

) {

    public static SubmissionDetailResponse from(
            SubmissionInfo submissionInfo,
            CommentPageResponse commentPage,
            Boolean isWriter,
            String field,
            Long challengeId,
            String challengeTitle) {
        return new SubmissionDetailResponse(
                submissionInfo,
                isWriter,
                field,
                challengeId,
                challengeTitle,
                commentPage

        );
    }
}
