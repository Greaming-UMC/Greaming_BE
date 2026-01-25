package com.umc.greaming.domain.auth.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.auth.dto.response.ReissueResponse;
import com.umc.greaming.domain.auth.dto.response.TokenResponse;
import com.umc.greaming.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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
        String redirectUrl = "/oauth2/authorize/" + provider.toLowerCase();
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueToken(
            @RequestHeader(REFRESH_TOKEN_HEADER) String refreshToken
    ) {
        ReissueResponse reissueResponse = authService.reissueTokens(refreshToken);

        // 액세스 토큰은 바디로, 리프레시 토큰은 헤더로
        TokenResponse bodyResponse = TokenResponse.of(
                reissueResponse.accessToken(),
                reissueResponse.accessTokenExpiresIn()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(REFRESH_TOKEN_HEADER, reissueResponse.refreshToken());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new com.umc.greaming.common.response.ApiResponse<>(
                        true,
                        SuccessStatus.CREATE_TOKEN_SUCCESS.getCode(),
                        SuccessStatus.CREATE_TOKEN_SUCCESS.getMessage(),
                        bodyResponse
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId
    ) {
        authService.logout(userId);
        return ApiResponse.success(SuccessStatus.LOGOUT_SUCCESS);
    }
}