package com.umc.greaming.domain.comment.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.response.ReplyResponse;
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

    @Operation(summary = "답글 목록 조회", description = "특정 댓글에 달린 답글 리스트를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 댓글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMENT404\", \"message\": \"댓글을 찾을 수 없습니다.\", \"result\": null}"
                            )
                    )
            )
    })
    @GetMapping("/{commentId}/replies")
    ResponseEntity<ApiResponse<ReplyResponse>> getReplyList(
            @Parameter(description = "댓글 ID") @Positive @PathVariable("commentId") Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "댓글 작성", description = "게시글에 새로운 댓글을 작성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"댓글 내용은 필수입니다.\", \"result\": null}"
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
                                    value = "{\"isSuccess\": false, \"code\": \"SUBMISSION404\", \"message\": \"존재하지 않는 게시글입니다.\", \"result\": null}"
                            )
                    )
            )
    })
    @PostMapping
    ResponseEntity<ApiResponse<String>> createComment(
            @RequestBody @Valid CommentCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "답글 작성", description = "특정 댓글에 답글을 작성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"답글 내용은 필수입니다.\", \"result\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 댓글 (부모 댓글 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMENT404\", \"message\": \"댓글을 찾을 수 없습니다.\", \"result\": null}"
                            )
                    )
            )
    })
    @PostMapping("/{commentId}/replies")
    ResponseEntity<ApiResponse<String>> createReply(
            @Parameter(description = "부모 댓글 ID") @PathVariable("commentId") Long commentId,
            @RequestBody @Valid ReplyCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );
}