package com.umc.greaming.domain.user.dto.response;

import com.umc.greaming.domain.user.entity.enums.UsagePurpose;

import java.util.List;

public record UserInfoResponse(
        String nickname,
        String intro,
        String profileImgUrl,
        List<String> specialtyTags,
        List<String> interestTags,
        UsagePurpose usagePurpose,
        Integer weeklyGoalScore
) {}
