package com.umc.greaming.domain.calendar.dto.response;

import java.util.List;

public record ChallengeCalendarResponse(
        List<ParticipatedAtResponse> dailyChallenge,
        List<ParticipatedAtResponse> weeklyChallenge
) {}