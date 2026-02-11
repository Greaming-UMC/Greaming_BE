package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 상세 조회 응답 DTO")
public record SubmissionDetailResponse(
        @Schema(description = "게시글 핵심 정보 (작성자, 이미지, 내용 등)")
        SubmissionInfo submission,

        @Schema(description = "댓글 페이징 데이터")
        CommentPageResponse commentPage,

        @Schema(description = "내가 작성한 게시물인지 여부")
        Boolean isWriter
) {

    public static SubmissionDetailResponse from(
            SubmissionInfo submissionInfo,
            CommentPageResponse commentPage,
            Boolean isWriter) {
        return new SubmissionDetailResponse(
                submissionInfo,
                commentPage,
                isWriter
        );
    }
}