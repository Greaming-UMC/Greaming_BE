package com.umc.greaming.domain.auth.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.auth.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth API", description = "인증 관련 API (토큰 재발급, 로그아웃)")
@RequestMapping("/api/auth")
public interface AuthApi {

    @Operation(summary = "인증 테스트", description = "현재 인증된 사용자의 ID를 반환합니다. 토큰 유효성 확인용 테스트 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "인증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = @ExampleObject(value = "1")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_401",
                                      "message": "유효하지 않은 토큰입니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/test")
    ResponseEntity<Long> getUserId(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "토큰 재발급",
            description = """
                    리프레시 토큰(HttpOnly 쿠키)을 사용하여 새로운 액세스 토큰을 발급합니다.

                    - 리프레시 토큰은 쿠키에서 자동으로 전송됩니다.
                    - 새로운 리프레시 토큰이 쿠키로 설정됩니다.
                    - 새로운 액세스 토큰은 응답 body로 반환됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "AUTH_200",
                                      "message": "토큰 재발급 성공",
                                      "result": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "expiresIn": 3600
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "리프레시 토큰 누락",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_400",
                                      "message": "리프레시 토큰이 필요합니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "리프레시 토큰 만료 또는 유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_401",
                                      "message": "리프레시 토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/reissue")
    ResponseEntity<ApiResponse<TokenResponse>> reissueToken(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    );

    @Operation(summary = "로그아웃", description = "현재 사용자의 리프레시 토큰을 DB에서 삭제하고 쿠키를 만료시킵니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "AUTH_200",
                                      "message": "로그아웃 성공",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMM_401",
                                      "message": "인증이 필요합니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    );
}
