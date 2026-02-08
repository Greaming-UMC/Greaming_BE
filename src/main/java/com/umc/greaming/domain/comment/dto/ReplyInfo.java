package com.umc.greaming.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.greaming.domain.comment.entity.Reply;

import java.time.format.DateTimeFormatter;

public record ReplyInfo(
        @JsonProperty("replyId")
        Long replyId,

        @JsonProperty("writer_nickname")
        String writerNickname,

        @JsonProperty("writer_profileImgUrl")
        String writerProfileImgUrl,

        @JsonProperty("content")
        String content,

        @JsonProperty("createdAt")
        String createdAt,

        @JsonProperty("isWriter") // (선택) 본인이 쓴 글인지 여부
        Boolean isWriter
) {
    public static ReplyInfo of(Reply reply, String profileUrl, boolean isWriter) {
        return new ReplyInfo(
                reply.getId(),
                reply.getUser().getNickname(),
                profileUrl, // Service에서 변환된 URL 주입
                reply.getContent(),
                reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")), // 날짜 포맷팅
                isWriter
        );
    }
}