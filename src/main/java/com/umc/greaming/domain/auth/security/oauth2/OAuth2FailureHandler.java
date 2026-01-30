package com.umc.greaming.domain.auth.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.greaming.common.status.error.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.error("OAuth2 로그인 실패: {}", exception.getMessage(), exception);

        ErrorStatus errorStatus = ErrorStatus.OAUTH2_LOGIN_FAILED;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(errorStatus.getHttpStatus().value());

        Map<String, Object> body = new HashMap<>();
        body.put("isSuccess", false);
        body.put("code", errorStatus.getCode());
        body.put("message", errorStatus.getMessage());
        body.put("result", null);

        objectMapper.writeValue(response.getWriter(), body);
    }
}