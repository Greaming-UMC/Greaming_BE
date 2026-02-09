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
        String writerProfileImgUrl, // <--- S3 URL이 들어갈 자리

        @JsonProperty("content")
        String content,

        @JsonProperty("createdAt")
        String createdAt,

        @JsonProperty("isWriter") // 프론트엔드가 수정/삭제 버튼 띄울지 판단하는 용도
        Boolean isWriter
) {
    // [수정] 메서드명을 Service 코드와 맞춤 (of -> from)
    public static ReplyInfo from(Reply reply, String profileUrl, boolean isWriter) {
        return new ReplyInfo(
                reply.getId(),
                reply.getUser().getNickname(),
                profileUrl, // Service에서 변환해온 URL을 여기에 쏙 넣습니다!
                reply.getContent(),
                reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")), // 예쁘게 포맷팅
                isWriter
        );
    }
}