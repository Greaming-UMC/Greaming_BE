package com.umc.greaming.domain.auth.dto.response;

public record ReissueResponse(
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn
) {
    public static ReissueResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return new ReissueResponse(accessToken, refreshToken, expiresIn);
    }
}