package com.umc.greaming.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull(message = "게시글 ID는 필수입니다.")
        Long submissionId,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
) {}