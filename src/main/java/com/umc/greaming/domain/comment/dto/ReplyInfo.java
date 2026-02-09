package com.umc.greaming.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.comment.entity.Reply;

import java.time.format.DateTimeFormatter;

public record ReplyInfo(
        @JsonProperty("replyId")
        Long replyId,

        @JsonProperty("userId")
        Long userId,

        @JsonProperty("writer_nickname")
        String writerNickname,

        @JsonProperty("writer_profileImgUrl")
        String writerProfileImgUrl,

        @JsonProperty("content")
        String content,

        @JsonProperty("createdAt")
        String createdAt,

        @JsonProperty("isWriter")
        Boolean isWriter
) {
    public static ReplyInfo from(Reply reply, String profileUrl, boolean isWriter) {
        return new ReplyInfo(
                reply.getId(),
                reply.getUser().getUserId(),
                reply.getUser().getNickname(),
                profileUrl,
                reply.getContent(),
                reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")),
                isWriter
        );
    }
}