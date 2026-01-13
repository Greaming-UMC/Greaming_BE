package com.umc.greaming.domain.work.dto.response;

import com.umc.greaming.domain.work.entity.Work;

import java.time.LocalDateTime;

public record WorkPreviewResponse(
        Long workId,
        String title,
        String cover,
        Integer likeCount,
        Integer commentCount,
        LocalDateTime createdAt
) {
    public static WorkPreviewResponse from(Work work) {
        return new WorkPreviewResponse(
                work.getId(),
                work.getTitle(),
                work.getCover(),
                work.getLikeCount(),
                work.getCommentCount(),
                work.getCreatedAt()
        );
    }
}
