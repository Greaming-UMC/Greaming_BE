package com.umc.greaming.domain.auth.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.auth.dto.response.ReissueResponse;
import com.umc.greaming.domain.auth.dto.response.TokenResponse;
import com.umc.greaming.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

    private final AuthService authService;

    @GetMapping("/login/social/{provider}")
    public void redirectToSocialLogin(
            @PathVariable String provider,
            HttpServletResponse response
    ) throws IOException {
        // 서비스 계층에서 프로바이더 검증
        authService.validateSocialProvider(provider);

        String redirectUrl = "/oauth2/authorization/" + provider.toLowerCase();
        response.sendRedirect(redirectUrl);
    }

    /**
     * 토큰 재발급 API
     * @param refreshToken 헤더로 전달받은 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰
     * @throws GeneralException 리프레시 토큰이 null, 빈 문자열, 또는 공백만 있는 경우
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueToken(
            @RequestHeader(value = REFRESH_TOKEN_HEADER, required = false) String refreshToken
    ) {
        // 리프레시 토큰 null, 빈 문자열, 공백 검증
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_MISSING);
        }

        ReissueResponse reissueResponse = authService.reissueTokens(refreshToken);

        TokenResponse bodyResponse = TokenResponse.of(
                reissueResponse.accessToken(),
                reissueResponse.accessTokenExpiresIn()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(REFRESH_TOKEN_HEADER, reissueResponse.refreshToken());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ApiResponse<>(
                        true,
                        SuccessStatus.CREATE_TOKEN_SUCCESS.getCode(),
                        SuccessStatus.CREATE_TOKEN_SUCCESS.getMessage(),
                        bodyResponse
                ));
    }

    /**
     * 로그아웃 API
     * <p>인증된 사용자만 접근 가능합니다. SecurityFilterChain에서 인증을 먼저 검증합니다.</p>
     *
     * @param userId 현재 인증된 사용자의 ID (Spring Security에서 자동 주입)
     * @return 로그아웃 성공 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId
    ) {
        authService.logout(userId);
        return ApiResponse.success(SuccessStatus.LOGOUT_SUCCESS);
    }
}