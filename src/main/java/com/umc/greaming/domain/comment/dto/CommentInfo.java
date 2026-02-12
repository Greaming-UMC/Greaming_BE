package com.umc.greaming.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.comment.entity.Comment;

public record CommentInfo(
        @JsonProperty("comment_id")
        Long commentId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("writer_nickname")
        String writerNickname,

        @JsonProperty("writer_profileImgUrl")
        String writerProfileImgUrl,

        String content,

        @JsonProperty("isLiked")
        Boolean isLiked,

        @JsonProperty("isWriter")
        Boolean isWriter
) {
    public static CommentInfo from(Comment comment, String profileUrl, boolean isLiked, boolean isWriter) {
        boolean deleted = comment.getUser().isDeleted();
        return new CommentInfo(
                comment.getId(),
                comment.getUser().getUserId(),
                deleted ? "삭제된 사용자" : comment.getUser().getNickname(),
                deleted ? null : profileUrl,
                comment.getContent(),
                isLiked,
                isWriter
        );
    }
}