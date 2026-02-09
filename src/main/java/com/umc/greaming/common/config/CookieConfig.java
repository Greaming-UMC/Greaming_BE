package com.umc.greaming.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * 쿠키 설정을 통합 관리하는 컴포넌트.
 * 리프레시 토큰 쿠키의 생성, 삭제를 일관되게 처리합니다.
 */
@Component
public class CookieConfig {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7일 (초 단위)

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    /**
     * 리프레시 토큰 쿠키를 생성하여 응답에 추가합니다.
     *
     * @param response     HttpServletResponse
     * @param request      HttpServletRequest (Secure 플래그 결정용)
     * @param refreshToken 리프레시 토큰 값
     */
    public void addRefreshTokenCookie(HttpServletResponse response, HttpServletRequest request, String refreshToken) {
        boolean isSecure = isSecureEnvironment(request);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(REFRESH_TOKEN_COOKIE_MAX_AGE)
                .sameSite(isSecure ? "None" : "Lax") // cross-site 요청 시 None 필요 (Secure 필수)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 리프레시 토큰 쿠키를 삭제합니다. (maxAge=0으로 즉시 만료)
     *
     * @param response HttpServletResponse
     * @param request  HttpServletRequest (Secure 플래그 결정용)
     */
    public void deleteRefreshTokenCookie(HttpServletResponse response, HttpServletRequest request) {
        boolean isSecure = isSecureEnvironment(request);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite(isSecure ? "None" : "Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 현재 환경이 Secure 쿠키를 사용해야 하는지 결정합니다.
     * - prod 프로필: 항상 true
     * - 그 외: request.isSecure() 기반 (HTTPS 요청인 경우 true)
     */
    private boolean isSecureEnvironment(HttpServletRequest request) {
        if ("prod".equals(activeProfile) || "dev".equals(activeProfile)) {
            return true;
        }
        // 로컬 환경에서는 요청 스킴에 따라 결정
        return request != null && request.isSecure();
    }

    /**
     * 리프레시 토큰 쿠키의 최대 유효 기간(초)을 반환합니다.
     */
    public int getRefreshTokenCookieMaxAge() {
        return REFRESH_TOKEN_COOKIE_MAX_AGE;
    }
}