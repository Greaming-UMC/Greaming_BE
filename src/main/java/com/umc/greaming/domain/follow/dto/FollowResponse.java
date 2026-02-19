package com.umc.greaming.domain.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팔로우 토글 응답 DTO")
public record FollowResponse(
        @Schema(description = "현재 팔로우 상태 (true: 팔로우 중, false: 팔로우 취소됨)", example = "true")
        boolean isFollowing,

        @Schema(description = "현재 팔로워 수", example = "42")
        long followerCount
) {
    public static FollowResponse of(boolean isFollowing, long followerCount) {
        return new FollowResponse(isFollowing, followerCount);
    }
}
