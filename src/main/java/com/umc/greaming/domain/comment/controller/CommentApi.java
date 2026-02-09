package com.umc.greaming.domain.comment.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.dto.response.ReplyResponse;
import com.umc.greaming.domain.comment.dto.ReplyInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API", description = "댓글 및 답글 관련 API")
@Validated
@RequestMapping("/api/comments")
public interface CommentApi {

    @Operation(summary = "댓글 생성", description = "게시글에 새로운 댓글을 작성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMENT_200",
                                      "message": "댓글 생성 성공",
                                      "result": {
                                        "commentId": 10,
                                        "writer_nickname": "그림쟁이",
                                        "writer_profileImgUrl": "https://s3.ap-northeast-2.amazonaws.com/greaming/profile/test.jpg",
                                        "content": "작품이 너무 멋져요!",
                                        "createdAt": "2024.02.09 18:30",
                                        "isWriter": true,
                                        "isLiked": false
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON400",
                                      "message": "댓글 내용은 필수입니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION404",
                                      "message": "존재하지 않는 게시글입니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping
    ResponseEntity<ApiResponse<CommentInfo>> createComment(
            @RequestBody @Valid CommentCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "답글 목록 조회", description = "특정 댓글의 답글(대댓글) 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "다음 댓글 불러오기 성공(답글 목록)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMENT_200",
                                      "message": "다음 댓글 불러오기 성공",
                                      "result": {
                                        "replies": [
                                          {
                                            "replyId": 25,
                                            "writer_nickname": "답글러",
                                            "writer_profileImgUrl": "https://s3.ap-northeast-2.amazonaws.com/greaming/profile/reply_user.jpg",
                                            "content": "저도 동감합니다.",
                                            "createdAt": "2024.02.09 19:00",
                                            "isWriter": false
                                          }
                                        ]
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 댓글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMENT404",
                                      "message": "존재하지 않는 댓글입니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{commentId}/replies")
    ResponseEntity<ApiResponse<ReplyResponse>> getReplyList(
            @Parameter(description = "부모 댓글 ID") @Positive @PathVariable("commentId") Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "답글 생성", description = "특정 댓글에 답글을 작성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "답글 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMENT_200",
                                      "message": "댓글 생성 성공",
                                      "result": {
                                        "replyId": 27,
                                        "writer_nickname": "내닉네임",
                                        "writer_profileImgUrl": "https://s3.ap-northeast-2.amazonaws.com/greaming/profile/my_image.jpg",
                                        "content": "새로운 답글입니다.",
                                        "createdAt": "2024.02.09 19:20",
                                        "isWriter": true
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 부모 댓글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMENT404",
                                      "message": "존재하지 않는 댓글입니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/{commentId}/replies")
    ResponseEntity<ApiResponse<ReplyInfo>> createReply(
            @Parameter(description = "부모 댓글 ID") @Positive @PathVariable("commentId") Long commentId,
            @RequestBody @Valid ReplyCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );
}