package com.umc.greaming.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        Long expiresIn
) {
    public static TokenResponse of(String accessToken, Long expiresIn) {
        return new TokenResponse(accessToken, expiresIn);
    }
}