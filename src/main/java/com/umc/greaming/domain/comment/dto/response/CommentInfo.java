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
    // [수정] 외부에서 변환된 URL과 좋아요 여부를 파라미터로 받음
    public static CommentInfo from(Comment comment, String profileUrl, boolean isLiked) {
        return new CommentInfo(
                comment.getUser().getNickname(),
                profileUrl, // S3 URL (Service에서 변환해서 넘겨줌)
                comment.getContent(),
                isLiked     // 좋아요 여부 (Service에서 조회해서 넘겨줌)
        );
    }
}