package com.umc.greaming.domain.calendar.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParticipatedAtResponse(
        @JsonProperty("participated_at")
        String participatedAt
) {}