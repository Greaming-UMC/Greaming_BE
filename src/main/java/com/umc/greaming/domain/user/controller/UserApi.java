package com.umc.greaming.domain.user.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import com.umc.greaming.domain.user.dto.request.UpdateUserInfoRequest;
import com.umc.greaming.domain.user.dto.response.UserInfoResponse;
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
import org.springframework.web.bind.annotation.*;


@Tag(name = "User API", description = "사용자 관련 API")
@RequestMapping("/api/user")
public interface UserApi {

    @Operation(summary = "프로필 등록 여부 확인", description = "현재 로그인한 사용자가 최초 프로필 정보를 등록했는지 여부를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 등록 여부 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "프로필 등록 여부 조회 성공",
                                      "result": {
                                        "profileRegistered": false
                                      }
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
            )
    })
    @GetMapping("/check-registered")
    ResponseEntity<ApiResponse<java.util.Map<String, Boolean>>> checkRegistered(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "최초 유저 정보 등록",
            description = """
                    소셜 로그인 후 최초 1회 사용자 정보를 등록합니다.

                    - 닉네임, 자기소개, 전문 분야 태그, 관심 분야 태그, 사용 목적, 주간 목표 점수를 설정합니다.
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

    @Operation(
            summary = "유저 정보 수정",
            description = """
                    등록된 유저 정보를 부분 수정합니다.

                    - 모든 필드는 nullable이며, null인 필드는 변경하지 않습니다.
                    - 태그(specialtyTags, interestTags)는 null이면 변경 없음, 값을 보내면 전체 교체됩니다.
                    - 최초 프로필 등록(registInfo)을 완료한 유저만 사용할 수 있습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "유저 정보 수정 성공",
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
                    description = "사용자 또는 프로필을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "USER_404",
                                      "message": "프로필 정보를 먼저 등록해주세요.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PatchMapping("/info")
    ResponseEntity<ApiResponse<Void>> updateInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @RequestBody @Valid UpdateUserInfoRequest request
    );

    @Operation(
            summary = "유저 정보 조회",
            description = """
                    유저의 프로필 정보를 조회합니다.

                    - 인증 없이 누구나 조회할 수 있습니다.
                    - 닉네임, 자기소개, 프로필 이미지, 태그, 사용 목적, 주간 목표 점수를 반환합니다.
                    - 프로필 미등록 유저는 404 에러가 반환됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "유저 정보 조회 성공",
                                      "result": {
                                        "nickname": "그림쟁이",
                                        "intro": "그림 그리는 것을 좋아합니다.",
                                        "profileImgUrl": "https://s3.amazonaws.com/...",
                                        "specialtyTags": ["일러스트", "캐릭터"],
                                        "interestTags": ["풍경", "인물"],
                                        "usagePurpose": "PAINTER",
                                        "weeklyGoalScore": 5
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "USER_404",
                                      "message": "프로필 정보를 먼저 등록해주세요.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{userId}/info")
    ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @Parameter(description = "조회할 유저 ID") @PathVariable Long userId
    );
}