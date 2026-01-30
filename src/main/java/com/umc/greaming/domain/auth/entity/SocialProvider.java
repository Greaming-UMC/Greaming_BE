package com.umc.greaming.domain.auth.entity;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    KAKAO("kakao"),
    GOOGLE("google");

    private final String value;

    public static SocialProvider from(String value) {
        return Arrays.stream(values())
                .filter(provider -> provider.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_SOCIAL_PROVIDER));
    }

    public String getValue() {
        return value;
    }
}
