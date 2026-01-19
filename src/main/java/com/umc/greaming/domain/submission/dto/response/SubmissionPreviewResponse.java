package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.submission.entity.Submission;

import java.util.List;

public record SubmissionPreviewResponse(
        Long workId,
        String thumbnailUrl,
        List<String> tags
) {
    public static SubmissionPreviewResponse from(Submission work, List<String> tags) {
        return new SubmissionPreviewResponse(
                work.getId(),
                work.getCover(), // cover를 thumbnailUrl로 내려줌
                tags
        );
    }
}
