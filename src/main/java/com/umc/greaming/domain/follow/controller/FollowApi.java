package com.umc.greaming.domain.follow.controller;

import com.umc.greaming.common.response.ApiResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
