package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;

public record SubmissionDetailResponse(
        SubmissionInfo submission,
        CommentPageResponse commentPage
) {
    public static SubmissionDetailResponse from(
            SubmissionInfo submissioninfo,
            Page<Comment> commentPage) {
        return new SubmissionDetailResponse(
                submissioninfo,
                CommentPageResponse.from(commentPage) // 위에서 만든 DTO 사용
        );
    }
}