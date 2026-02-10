package com.umc.greaming.common.config;

import com.umc.greaming.domain.auth.security.JwtAuthenticationEntryPoint;
import com.umc.greaming.domain.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2Config oAuth2Config;

    private final String[] PUBLIC_URLS = {
            "/api/auth/reissue",
            "/api/auth/dev/**",
            "/oauth2/**",
            "/login/oauth2/**",
            "/h2-console/**",
            "/api/works/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",

            //홈화면 퍼블릭으로 추가하였습니다 - mybookG
            "/api/home/**",
            "/api/challenges/**",
            "/api/submissions"
    };
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/h2-console/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                );
        // [수정] /api/submissions/** 제거 - 인증이 필요한 경로임
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                PUBLIC_URLS
                        ).permitAll()
                        .requestMatchers(
                                "/api/submissions/*/preview"  // 게시글 미리보기는 공개
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET, "/api/user/*/info"  // 유저 정보 조회는 공개
                        ).permitAll()
                        .requestMatchers(
                                "/actuator/**"
                        )
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oAuth2Config::configure)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}