package com.umc.greaming.domain.user.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User API", description = "사용자 관련 API")
@RequestMapping("/api/user")
public interface UserApi {

    @Operation(
            summary = "최초 유저 정보 등록",
            description = """
                    소셜 로그인 후 최초 1회 사용자 정보를 등록합니다.

                    - 닉네임, 자기소개, 전문 분야/스타일, 관심 분야/스타일, 사용 목적, 주간 목표 점수를 설정합니다.
                    - 이미 정보가 등록된 유저는 409 에러가 반환됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "최초 유저 정보 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "최초 유저 정보 등록 성공",
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_404",
                                      "message": "회원을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 정보가 등록된 유저",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "USER_409",
                                      "message": "이미 정보가 등록된 유저입니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/registinfo")
    ResponseEntity<ApiResponse<Void>> registInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @RequestBody @Valid RegistInfoRequest request
    );
}