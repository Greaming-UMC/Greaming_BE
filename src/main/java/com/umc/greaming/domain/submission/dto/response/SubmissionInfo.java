package com.umc.greaming.domain.submission.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.tag.dto.TagInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시글 핵심 정보 DTO")
public record SubmissionInfo(
        @Schema(description = "작성자 닉네임", example = "그림쟁이")
        String nickname,

        @Schema(description = "작성자 프로필 이미지 URL (변환됨)", example = "https://s3.../profiles/user1.jpg")
        String profileImageUrl,

        @Schema(description = "작성자 레벨", example = "Painter")
        String level,

        @Schema(description = "게시글 이미지 URL 목록 (정렬됨)", example = "[\"https://s3.../img1.jpg\", \"https://s3.../img2.jpg\"]")
        List<String> imageList,

        @Schema(description = "좋아요 수", example = "10")
        Integer likesCount,

        @Schema(description = "댓글 수", example = "5")
        Integer commentCount,

        @Schema(description = "북마크 수", example = "3")
        Integer bookmarkCount,

        @Schema(description = "게시글 제목", example = "나의 작품")
        String title,

        @Schema(description = "게시글 본문", example = "설명입니다.")
        String caption,

        @Schema(description = "태그 정보 목록")
        List<TagInfo> tags, // [수정] String -> TagInfo로 변경 및 중복 제거

        @Schema(description = "현재 사용자의 좋아요 여부", example = "false")
        Boolean liked,

        @Schema(description = "업로드 일시", example = "2026-02-03T18:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime uploadAt
) {
    // Factory Method
    public static SubmissionInfo from(Submission submission,
                                      String profileImageUrl,
                                      String level,
                                      List<String> sortedImages,
                                      List<TagInfo> tags,
                                      boolean isLiked) {
        return new SubmissionInfo(
                submission.getUser().getNickname(),
                profileImageUrl,
                level,
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