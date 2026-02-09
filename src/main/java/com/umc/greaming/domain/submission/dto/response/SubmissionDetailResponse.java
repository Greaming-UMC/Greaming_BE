package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.comment.dto.response.CommentPageResponse; // Import 확인
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 상세 조회 응답 DTO")
public record SubmissionDetailResponse(
        @Schema(description = "게시글 핵심 정보 (작성자, 이미지, 내용 등)")
        SubmissionInfo submission,

        @Schema(description = "댓글 페이징 데이터")
        CommentPageResponse commentPage
) {

    public static SubmissionDetailResponse from(
            SubmissionInfo submissionInfo,
            CommentPageResponse commentPage) {
        return new SubmissionDetailResponse(
                submissionInfo,
                commentPage
        );
    }
}