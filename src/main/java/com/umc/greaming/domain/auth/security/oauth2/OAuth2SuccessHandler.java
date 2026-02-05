package com.umc.greaming.domain.auth.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.auth.dto.response.LoginResponse;
import com.umc.greaming.domain.auth.security.JwtTokenProvider;
import com.umc.greaming.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId);

        // 리프레시 토큰 생성 및 DB 저장
        String refreshToken = authService.createAndSaveRefreshToken(userId);

        log.info("OAuth2 로그인 성공: userId={}, isNewUser={}", userId, oAuth2User.isNewUser());

        // 리프레시 토큰은 헤더로 전달
        response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);

        // 액세스 토큰은 바디로 전달
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);

        LoginResponse loginResponse = LoginResponse.of(
                accessToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                oAuth2User.isNewUser()
        );

        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(
                true,
                SuccessStatus.LOGIN_SUCCESS.getCode(),
                SuccessStatus.LOGIN_SUCCESS.getMessage(),
                loginResponse
        );

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}