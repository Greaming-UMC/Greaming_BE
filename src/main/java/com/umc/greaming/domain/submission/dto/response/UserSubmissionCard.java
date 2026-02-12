package com.umc.greaming.domain.submission.dto.response;

import com.umc.greaming.domain.submission.entity.Submission;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 게시글 카드 정보")
public record UserSubmissionCard(
        @Schema(description = "게시글 ID", example = "7")
        Long submissionId,

        @Schema(description = "썸네일 이미지 URL", example = "https://s3.../thumbnail.jpg")
        String thumbnailUrl,

        @Schema(description = "작성자 유저 ID", example = "1")
        Long userId,

        @Schema(description = "작성자 닉네임", example = "Picasso")
        String nickname,

        @Schema(description = "작성자 프로필 이미지 URL", example = "https://s3.../profile.jpg")
        String profileImageUrl,

        @Schema(description = "좋아요 수", example = "42")
        Integer likesCount,

        @Schema(description = "댓글 수", example = "10")
        Integer commentCount,

        @Schema(description = "북마크 수", example = "5")
        Integer bookmarkCount
) {
    public static UserSubmissionCard from(Submission submission, String thumbnailUrl, String profileImageUrl) {
        return new UserSubmissionCard(
                submission.getId(),
                thumbnailUrl,
                submission.getUser().getUserId(),
                submission.getUser().getNickname(),
                profileImageUrl,
                submission.getLikeCount(),
                submission.getCommentCount(),
                submission.getBookmarkCount()
        );
    }
}
