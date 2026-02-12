package com.umc.greaming.domain.auth.dto.response;

public record LoginResponse(
        String accessToken,
        Long accessTokenExpiresIn,
        boolean isNewUser
) {
    public static LoginResponse of(String accessToken, Long expiresIn, boolean isNewUser) {
        return new LoginResponse(accessToken, expiresIn, isNewUser);
    }
}
