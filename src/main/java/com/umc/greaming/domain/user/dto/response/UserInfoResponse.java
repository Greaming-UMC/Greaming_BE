package com.umc.greaming.domain.user.dto.response;

import com.umc.greaming.domain.challenge.enums.JourneyLevel;

import java.util.List;

public record UserInfoResponse(
        String nickname,
        String intro,
        String profileImgUrl,
        List<String> specialtyTags,
        List<String> interestTags,
        JourneyLevel journeyLevel,
        Integer weeklyGoalScore
) {}
