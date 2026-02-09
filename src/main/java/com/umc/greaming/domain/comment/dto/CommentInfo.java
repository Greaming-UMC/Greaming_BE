package com.umc.greaming.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.comment.entity.Comment;

public record CommentInfo(
        @JsonProperty("writer_nickname")
        String writerNickname,

        @JsonProperty("writer_profileImgUrl")
        String writerProfileImgUrl,

        String content,

        @JsonProperty("isLiked")
        Boolean isLiked,

        // ▼▼▼ [추가] 본인 작성 여부 필드
        @JsonProperty("isWriter")
        Boolean isWriter
) {
    // [수정] isWriter 파라미터 추가
    public static CommentInfo from(Comment comment, String profileUrl, boolean isLiked, boolean isWriter) {
        return new CommentInfo(
                comment.getUser().getNickname(),
                profileUrl,
                comment.getContent(),
                isLiked,
                isWriter // [추가] 값 주입
        );
    }
}