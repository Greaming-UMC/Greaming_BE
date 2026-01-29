package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.submission.entity.Submission;

import java.util.List;

public record SubmissionPreviewResponse(
        Long submissionId,
        String thumbnailUrl,
        List<String> tags
) {
    public static SubmissionPreviewResponse from(Submission sub, List<String> tags) {
        return new SubmissionPreviewResponse(
                sub.getId(),
                sub.getThumbnailUrl(), // cover를 thumbnailUrl로 내려줌 -- 이거 커버가 아닌것 같아서 바꿔봤습니다
                tags
        );
    }
}
