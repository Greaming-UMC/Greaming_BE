package com.umc.greaming.domain.comment.dto.response;

import com.umc.greaming.domain.comment.dto.ReplyInfo;

import java.util.List;

public record ReplyResponse(
        List<ReplyInfo> replies,
        int totalCount
) {
    public static ReplyResponse of(List<ReplyInfo> replies) {
        return new ReplyResponse(replies, replies.size());
    }
}