package com.umc.greaming.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.comment.entity.Comment;

public record CommentInfo(
        @JsonProperty("writer_nickname")
        String writerNickname,

        @JsonProperty("writer_profileImgUrl")
        String writerProfileImgUrl,

        String content,

        @JsonProperty("isLiked")
        Boolean isLiked
) {
    // 엔티티 -> DTO 변환 로직
    public static CommentInfo from(Comment comment) {
        return new CommentInfo(
                comment.getUser().getNickname(),
                comment.getUser().getProfileImageKey(),
                comment.getContent(),
                false // 좋아요
        );
    }
}