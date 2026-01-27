package com.umc.greaming.domain.submission.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.umc.greaming.domain.submission.entity.Submission;

import java.time.LocalDateTime;
import java.util.List;

public record SubmissionInfo(
        String nickname,
        String profileImageUrl,
        String level,

        List<String> imageList,

        Integer likesCount,
        Integer commentCount,
        Integer bookmarkCount,

        String title,
        String caption,
        List<String> tags,
        Boolean liked,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime uploadAt
) {
    public static SubmissionInfo from(Submission submission, List<String> sortedImages, List<String> tags, boolean isLiked) {
        return new SubmissionInfo(
                submission.getUser().getNickname(),
                submission.getUser().getProfileImageUrl(),
                "Painter",
                sortedImages,
                submission.getLikeCount(),
                submission.getCommentCount(),
                submission.getBookmarkCount(),
                submission.getTitle(),
                submission.getCaption(),
                tags,
                isLiked,
                submission.getCreatedAt()
        );
    }
}