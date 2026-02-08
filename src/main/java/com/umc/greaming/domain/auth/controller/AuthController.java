package com.umc.greaming.domain.auth.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.auth.dto.response.ReissueResponse;
import com.umc.greaming.domain.auth.dto.response.TokenResponse;
import com.umc.greaming.domain.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7일 (초 단위)

    private final AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<Long> getUserId(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userId);
    }

    /**
     * 토큰 재발급 API
     * @param request 쿠키에서 리프레시 토큰을 읽기 위한 요청 객체
     * @param response 새로운 리프레시 토큰을 쿠키로 설정하기 위한응답 객체
     * @return 새로운 액세스 토큰
     * @throws GeneralException 리프레시 토큰이 없는 경우
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 쿠키에서 리프레시 토큰 추출
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_MISSING);
        }

        ReissueResponse reissueResponse = authService.reissueTokens(refreshToken);

        // 새로운 리프레시 토큰을 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, reissueResponse.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
        response.addCookie(refreshTokenCookie);

        TokenResponse bodyResponse = TokenResponse.of(
                reissueResponse.accessToken(),
                reissueResponse.accessTokenExpiresIn()
        );

        return ApiResponse.success(SuccessStatus.CREATE_TOKEN_SUCCESS, bodyResponse);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 로그아웃 API
     * <p>인증된 사용자만 접근 가능합니다. SecurityFilterChain에서 인증을 먼저 검증합니다.</p>
     *
     * @param userId 현재 인증된 사용자의 ID (Spring Security에서 자동 주입)
     * @param response 쿠키 삭제를 위한 응답 객체
     * @return 로그아웃 성공 응답
     * @throws GeneralException 인증 정보가 없는 경우 (UNAUTHORIZED)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response
    ) {
        // Spring Security 필터를 통과했음에도 userId가 null인 경우 (비정상 상황)
        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        authService.logout(userId);

        // 리프레시 토큰 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie);

        return ApiResponse.success(SuccessStatus.LOGOUT_SUCCESS);
    }
}