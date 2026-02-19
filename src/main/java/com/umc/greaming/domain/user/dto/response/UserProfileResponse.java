package com.umc.greaming.domain.user.dto.response;

public record UserProfileResponse(
        UserInfoResponse userInfo,
        long followerCount,
        long followingCount
) {}
