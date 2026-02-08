package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
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

@Tag(name = "Submission API", description = "게시글 관련 API")
@Validated
@RequestMapping("/api/submissions") // [추가] 공통 URL 경로
public interface SubmissionApi {

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 등록합니다. (썸네일, 분야 필수)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"제목은 필수입니다.\", \"result\": null}"
                            )
                    )
            )
    })
    @PostMapping // [추가] 메서드 매핑
    ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody @Valid SubmissionCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "게시글 미리보기 조회", description = "게시글 ID를 통해 미리보기 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
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
    @GetMapping("/{submissionId}/preview") // [추가]
    ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(
            @Parameter(description = "게시글 ID") @Positive @PathVariable("submissionId") Long submissionId // [추가] @PathVariable 필수
    );

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID와 페이지 번호를 통해 상세 정보를 조회합니다. (첫 화면 로딩용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
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
    @GetMapping("/{submissionId}") // [추가]
    ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @Parameter(description = "게시글 ID") @Positive @PathVariable("submissionId") Long submissionId, // [추가] @PathVariable
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") @Positive int page,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "댓글 목록 조회 (페이징)", description = "게시글의 댓글만 따로 조회합니다. (스크롤/더보기 기능용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
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
    @GetMapping("/{submissionId}/comments") // [추가]
    ResponseEntity<ApiResponse<CommentPageResponse>> getCommentList(
            @Parameter(description = "게시글 ID") @Positive @PathVariable("submissionId") Long submissionId, // [추가] @PathVariable
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") @Positive int page
    );

    @Operation(summary = "게시글 수정", description = "게시글의 제목, 내용, 태그, 이미지를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"제목은 필수입니다.\", \"result\": null}"
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "수정 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON403\", \"message\": \"게시글 수정 권한이 없습니다.\", \"result\": null}"
                            )
                    )
            )
    })
    @PutMapping("/{submissionId}") // [추가]
    ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @Parameter(description = "수정할 게시글 ID") @Positive @PathVariable("submissionId") Long submissionId, // [추가] @PathVariable
            @RequestBody @Valid SubmissionUpdateRequest updateSubmission,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "삭제 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON403\", \"message\": \"게시글 삭제 권한이 없습니다.\", \"result\": null}"
                            )
                    )
            )
    })
    @DeleteMapping("/{submissionId}") // [추가]
    ResponseEntity<ApiResponse<Long>> deleteSubmission(
            @Parameter(description = "삭제할 게시글 ID") @Positive @PathVariable("submissionId") Long submissionId, // [추가] @PathVariable
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );
}