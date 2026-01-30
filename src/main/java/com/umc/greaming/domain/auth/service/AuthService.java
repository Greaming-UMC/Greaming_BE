package com.umc.greaming.domain.auth.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.auth.dto.response.ReissueResponse;
import com.umc.greaming.domain.auth.entity.RefreshToken;
import com.umc.greaming.domain.auth.entity.SocialProvider;
import com.umc.greaming.domain.auth.repository.RefreshTokenRepository;
import com.umc.greaming.domain.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 소셜 로그인 프로바이더 검증
     * @param provider 소셜 프로바이더 문자열 (kakao, google)
     * @return 검증된 SocialProvider enum
     * @throws GeneralException 지원하지 않는 프로바이더인 경우
     */
    public SocialProvider validateSocialProvider(String provider) {
        return SocialProvider.from(provider);
    }

    @Transactional
    public String createAndSaveRefreshToken(Long userId) {
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(userId)
                                        .token(refreshToken)
                                        .expiresAt(expiresAt)
                                        .build()
                        )
                );

        return refreshToken;
    }

    @Transactional
    public ReissueResponse reissueTokens(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }

        // DB에서 리프레시 토큰 조회
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        // 만료 여부 확인
        if (savedToken.isExpired()) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = savedToken.getUserId();

        // 새 리프레시 토큰 만료 시간 계산 (JWT와 DB 만료일자 일치)
        LocalDateTime newExpiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        // 새 액세스 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        // 새 리프레시 토큰 발급 (새 만료일자 사용)
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // DB 업데이트 (JWT와 동일한 만료일자 사용)
        savedToken.updateToken(newRefreshToken, newExpiresAt);

        log.info("토큰 재발급: userId={}", userId);

        return ReissueResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessTokenExpiration()
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        SecurityContextHolder.clearContext();
        log.info("로그아웃: userId={}", userId);
    }
}
