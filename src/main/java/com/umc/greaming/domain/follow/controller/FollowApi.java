package com.umc.greaming.domain.follow.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.follow.dto.FollowListResponse;
import com.umc.greaming.domain.follow.dto.FollowResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Follow API", description = "팔로우 관련 API")
@Validated
@RequestMapping("/api/users")
public interface FollowApi {

    @Operation(summary = "팔로우 토글", description = """
            특정 유저를 팔로우하거나 팔로우를 취소합니다.
            
            - 이미 팔로우 중이라면 → 팔로우 취소
            - 팔로우하지 않았다면 → 팔로우 추가
            - 자기 자신은 팔로우 불가
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "팔로우 토글 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "FOLLOW_200",
                                      "message": "팔로우 상태가 변경되었습니다.",
                                      "result": {
                                        "isFollowing": true,
                                        "followerCount": 42
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "자기 자신 팔로우 시도",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "FOLLOW_400",
                                      "message": "자기 자신을 팔로우할 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 유저",
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
    @PostMapping("/{targetUserId}/follow")
    ResponseEntity<ApiResponse<FollowResponse>> toggleFollow(
            @Parameter(description = "팔로우할 유저 ID") @Positive @PathVariable("targetUserId") Long targetUserId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "팔로잉 목록 조회", description = "내가 팔로우하는 사람 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "팔로잉 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "FOLLOW_200",
                                      "message": "팔로잉 목록 조회 성공",
                                      "result": {
                                        "users": [
                                          {
                                            "userId": 2,
                                            "nickname": "그림쟁이",
                                            "profileImgUrl": "https://s3.../profile.jpg",
                                            "journeyLevel": "PAINTER",
                                            "specialtyTags": ["일러스트"],
                                            "interestTags": ["풍경"],
                                            "isFollower": true,
                                            "isFollowing": true
                                          }
                                        ],
                                        "pageInfo": {
                                          "currentPage": 1,
                                          "pageSize": 20,
                                          "totalPages": 3,
                                          "totalElements": 42,
                                          "isLast": false,
                                          "isFirst": true
                                        }
                                      }
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{userId}/followings")
    ResponseEntity<ApiResponse<FollowListResponse>> getFollowings(
            @Parameter(description = "조회할 유저 ID") @Positive @PathVariable("userId") Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long loginUserId,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") @Positive int page,
            @Parameter(description = "페이지 사이즈", example = "20")
            @RequestParam(defaultValue = "20") @Positive int size
    );

    @Operation(summary = "팔로워 목록 조회", description = "나를 팔로우하는 사람 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "팔로워 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "FOLLOW_200",
                                      "message": "팔로워 목록 조회 성공",
                                      "result": {
                                        "users": [
                                          {
                                            "userId": 3,
                                            "nickname": "수채화러",
                                            "profileImgUrl": "https://s3.../profile2.jpg",
                                            "journeyLevel": "SKETCHER",
                                            "specialtyTags": ["수채화"],
                                            "interestTags": ["인물"],
                                            "isFollower": true,
                                            "isFollowing": false
                                          }
                                        ],
                                        "pageInfo": {
                                          "currentPage": 1,
                                          "pageSize": 20,
                                          "totalPages": 2,
                                          "totalElements": 30,
                                          "isLast": false,
                                          "isFirst": true
                                        }
                                      }
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{userId}/followers")
    ResponseEntity<ApiResponse<FollowListResponse>> getFollowers(
            @Parameter(description = "조회할 유저 ID") @Positive @PathVariable("userId") Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long loginUserId,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") @Positive int page,
            @Parameter(description = "페이지 사이즈", example = "20")
            @RequestParam(defaultValue = "20") @Positive int size
    );
}
