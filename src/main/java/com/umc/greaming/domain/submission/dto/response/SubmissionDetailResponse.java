package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "게시글 상세 조회 응답 DTO")
public record SubmissionDetailResponse(
        @Schema(description = "게시글 핵심 정보 (작성자, 이미지, 내용 등)")
        SubmissionInfo submission,

        @Schema(description = "댓글 페이징 데이터 (기본 30개씩)")
        CommentPageResponse commentPage
) {
    public static SubmissionDetailResponse from(
            SubmissionInfo submissionInfo,
            Page<Comment> commentPage) {
        return new SubmissionDetailResponse(
                submissionInfo,
                CommentPageResponse.from(commentPage)
        );
    }
}