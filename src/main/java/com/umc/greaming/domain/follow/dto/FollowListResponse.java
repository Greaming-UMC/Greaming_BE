package com.umc.greaming.domain.follow.dto;

import com.umc.greaming.common.response.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "팔로워/팔로잉 목록 응답 DTO")
public record FollowListResponse(
        @Schema(description = "유저 카드 목록")
        List<FollowUserInfo> users,

        @Schema(description = "페이지 정보")
        PageInfo pageInfo
) {
    public static FollowListResponse of(List<FollowUserInfo> users, PageInfo pageInfo) {
        return new FollowListResponse(users, pageInfo);
    }
}
