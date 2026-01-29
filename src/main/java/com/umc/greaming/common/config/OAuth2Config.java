package com.umc.greaming.common.config;

import com.umc.greaming.domain.auth.security.oauth2.CustomOAuth2UserService;
import com.umc.greaming.domain.auth.security.oauth2.OAuth2FailureHandler;
import com.umc.greaming.domain.auth.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    public void configure(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
        oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService));
    }
}
