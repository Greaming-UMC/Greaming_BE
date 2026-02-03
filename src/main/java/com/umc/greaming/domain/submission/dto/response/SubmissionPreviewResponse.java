package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.submission.entity.Submission;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 목록 조회(프리뷰) 응답 DTO")
public record SubmissionPreviewResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long submissionId,

        @Schema(description = "썸네일 이미지 URL (변환됨)", example = "https://s3.../submissions/1/thumb.jpg")
        String thumbnailUrl,

        @Schema(description = "태그 목록 (최대 3개 등)", example = "[\"일러스트\", \"풍경\"]")
        List<String> tags
) {
    public static SubmissionPreviewResponse from(Submission sub, String thumbnailUrl, List<String> tags) {
        return new SubmissionPreviewResponse(
                sub.getId(),
                thumbnailUrl,
                tags
        );
    }
}