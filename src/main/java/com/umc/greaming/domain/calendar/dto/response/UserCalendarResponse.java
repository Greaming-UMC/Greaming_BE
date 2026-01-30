package com.umc.greaming.domain.calendar.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCalendarResponse(
        @JsonProperty("challenge_calendar")
        ChallengeCalendarResponse challengeCalendar
) {}