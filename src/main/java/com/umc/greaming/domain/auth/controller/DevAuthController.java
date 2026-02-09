package com.umc.greaming.domain.auth.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.auth.dto.response.ReissueResponse;
import com.umc.greaming.domain.auth.security.JwtTokenProvider;
import com.umc.greaming.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.umc.greaming.common.config.CookieConfig;

@Tag(name = "Dev Auth", description = "개발용 인증 API (dev/local 전용)")
@Profile({"dev", "local"})
@RestController
@RequestMapping("/api/auth/dev")
@RequiredArgsConstructor
public class DevAuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieConfig cookieConfig;

    @Operation(summary = "강제 로그인", description = "userId를 입력하면 소셜 로그인 없이 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ReissueResponse>> devLogin(
            @RequestParam Long userId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = authService.createAndSaveRefreshToken(userId);
        cookieConfig.addRefreshTokenCookie(response, request, refreshToken);

        String accessToken = jwtTokenProvider.createAccessToken(userId);
        ReissueResponse tokenResponse = ReissueResponse.of(
                accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiration());

        return ApiResponse.success(SuccessStatus.CREATE_TOKEN_SUCCESS, tokenResponse);
    }
}