package com.umc.greaming.domain.challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.submission.entity.Submission;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 게시물 카드 정보")
public record ChallengeSubmissionCard(
        @Schema(description = "게시물 ID", example = "100")
        @JsonProperty("submissionId")
        Long submissionId,

        @Schema(description = "썸네일 이미지 URL", example = "https://s3.../thumbnail.jpg")
        @JsonProperty("thumbnailUrl")
        String thumbnailUrl,

        @Schema(description = "작성자 ID", example = "1")
        @JsonProperty("userId")
        Long userId,

        @Schema(description = "작성자 닉네임", example = "그림쟁이")
        @JsonProperty("nickname")
        String nickname,

        @Schema(description = "작성자 프로필 이미지 URL", example = "https://s3.../profile.jpg")
        @JsonProperty("profileImageUrl")
        String profileImageUrl,

        @Schema(description = "좋아요 수", example = "10")
        @JsonProperty("likesCount")
        Integer likesCount,

        @Schema(description = "댓글 수", example = "5")
        @JsonProperty("commentCount")
        Integer commentCount,

        @Schema(description = "북마크 수", example = "100")
        @JsonProperty("bookmarkCount")
        Integer bookmarkCount
) {
    public static ChallengeSubmissionCard from(Submission submission, String thumbnailUrl, String profileImageUrl) {
        boolean deleted = submission.getUser().isDeleted();
        return new ChallengeSubmissionCard(
                submission.getId(),
                thumbnailUrl,
                submission.getUser().getUserId(),
                deleted ? "삭제된 사용자" : submission.getUser().getNickname(),
                deleted ? null : profileImageUrl,
                submission.getLikeCount(),
                submission.getCommentCount(),
                submission.getBookmarkCount()
        );
    }
}
