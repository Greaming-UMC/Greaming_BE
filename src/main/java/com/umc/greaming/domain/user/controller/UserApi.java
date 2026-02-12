package com.umc.greaming.domain.user.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import com.umc.greaming.domain.user.dto.request.UpdateUserInfoRequest;
import com.umc.greaming.domain.user.dto.response.UserInfoResponse;
import com.umc.greaming.domain.user.dto.response.UserSearchResponse;
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


@Tag(name = "User_API", description = "사용자 관련 API")
@RequestMapping("/api/users")
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

                    - 닉네임, 자기소개, 전문 분야 태그, 관심 분야 태그, Journey 레벨, 주간 목표 점수를 설정합니다.
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
                    - 닉네임, 자기소개, 프로필 이미지, 태그, Journey 레벨, 주간 목표 점수를 반환합니다.
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
                                        "journeyLevel": "PAINTER",
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

    @Operation(
            summary = "닉네임으로 유저 검색",
            description = """
                    닉네임 키워드로 유저를 검색하여 유저 ID 목록을 반환합니다.

                    - 닉네임에 키워드가 포함된 ACTIVE 상태의 유저를 검색합니다.
                    - 인증이 필요합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "유저 검색 성공",
                                      "result": {
                                        "users": [
                                          {
                                            "userId": 1,
                                            "nickname": "그림쟁이",
                                            "profileImgUrl": "https://s3.amazonaws.com/..."
                                          },
                                          {
                                            "userId": 5,
                                            "nickname": "그림그리는사람",
                                            "profileImgUrl": null
                                          }
                                        ]
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
            )
    })
    @GetMapping("/search")
    ResponseEntity<ApiResponse<UserSearchResponse>> searchByNickname(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "검색할 닉네임 키워드", example = "그림") @RequestParam String nickname
    );

    @Operation(
            summary = "본인 여부 확인",
            description = """
                    주어진 유저 ID가 현재 로그인한 사용자 본인인지 확인합니다.

                    - `isMe: true`이면 본인, `false`이면 다른 사용자입니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "본인 여부 확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "본인 여부 확인 성공",
                                      "result": {
                                        "isMe": true
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
            )
    })
    @GetMapping("/{targetUserId}/is-me")
    ResponseEntity<ApiResponse<java.util.Map<String, Boolean>>> checkIsMe(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "확인할 유저 ID") @PathVariable Long targetUserId
    );

    @Operation(
            summary = "내 프로필 상단 정보 조회",
            description = """
                    내 프로필 화면 상단 정보를 조회합니다.
                    
                    - 닉네임, 프로필 이미지, Journey 레벨, 자기소개
                    - 팔로워/팔로잉 수
                    - 전문 분야 태그, 관심 분야 태그
                    - 챌린지 캘린더 정보 (일일/주간)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 상단 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "프로필 상단 정보 조회 성공",
                                      "result": {
                                        "userInformation": {
                                          "nickname": "그림쟁이",
                                          "profileImgUrl": "https://s3.amazonaws.com/...",
                                          "level": "PAINTER",
                                          "introduction": "그림 그리는 것을 좋아합니다",
                                          "followerCount": 150,
                                          "followingCount": 80,
                                          "specialtyTags": ["일러스트", "캐릭터"],
                                          "interestTags": ["풍경", "인물"]
                                        },
                                        "challengeCalendar": {
                                          "dailyChallenge": [],
                                          "weeklyChallenge": []
                                        }
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
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping("/me")
    ResponseEntity<ApiResponse<com.umc.greaming.domain.user.dto.response.MyProfileTopResponse>> getMyProfileTop(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "닉네임 중복 확인",
            description = """
                    입력한 닉네임이 이미 사용 중인지 확인합니다.
                    
                    - 회원가입 또는 프로필 수정 시 사용
                    - `isAvailable: true`이면 사용 가능, `false`이면 이미 사용 중
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "닉네임 중복 확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "닉네임 중복 확인 성공",
                                      "result": {
                                        "isAvailable": true
                                      }
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/checkNickname")
    ResponseEntity<ApiResponse<java.util.Map<String, Boolean>>> checkNickname(
            @Parameter(description = "확인할 닉네임", example = "그림쟁이") @RequestParam String nickname
    );

    @Operation(
            summary = "내 프로필 설정 정보 조회",
            description = """
                    내 프로필 설정 화면에 필요한 정보를 조회합니다.
                    
                    - 닉네임, 프로필 이미지, Journey 레벨, 자기소개
                    - 전문 분야 태그, 관심 분야 태그
                    - 주간 목표 점수
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 설정 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "USER_200",
                                      "message": "프로필 설정 정보 조회 성공",
                                      "result": {
                                        "nickname": "그림쟁이",
                                        "profileImgUrl": "https://s3.amazonaws.com/...",
                                        "journeyLevel": "PAINTER",
                                        "introduction": "그림 그리는 것을 좋아합니다",
                                        "specialtyTags": ["일러스트", "캐릭터"],
                                        "interestTags": ["풍경", "인물"],
                                        "weeklyGoalScore": 5
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
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping("/me/profile")
    ResponseEntity<ApiResponse<com.umc.greaming.domain.user.dto.response.MyProfileSettingsResponse>> getMyProfileSettings(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "회원 탈퇴",
            description = """
                    현재 로그인한 사용자의 계정을 탈퇴 처리합니다.

                    - 소프트 삭제: 유저 상태를 DELETED로 변경합니다.
                    - 리프레시 토큰을 삭제하여 로그아웃 처리합니다.
                    - 기존 콘텐츠(게시물, 댓글 등)는 유지되며, 작성자는 '삭제된 사용자'로 표시됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "AUTH_200",
                                      "message": "회원탈퇴 성공",
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
            )
    })
    @DeleteMapping("/me")
    ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );
}
