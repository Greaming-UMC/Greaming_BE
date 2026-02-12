package com.umc.greaming.domain.auth.security.oauth2;

import com.umc.greaming.common.config.CookieConfig;
import com.umc.greaming.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final CookieConfig cookieConfig;

    @Value("${oauth2.redirect-uri}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();

        // 리프레시 토큰 생성 및 DB 저장
        String refreshToken = authService.createAndSaveRefreshToken(userId);

        log.info("OAuth2 로그인 성공: userId={}, isNewUser={}", userId, oAuth2User.isNewUser());

        // 리프레시 토큰은 HttpOnly 쿠키로 설정
        cookieConfig.addRefreshTokenCookie(response, request, refreshToken);

        // 프론트엔드로 리다이렉트 (is_new_user만 쿼리 파라미터로 전달)
        // 프론트에서 /api/auth/reissue를 호출하여 액세스 토큰을 발급받아야 함
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("is_new_user", oAuth2User.isNewUser())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}