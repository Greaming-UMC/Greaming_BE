package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Submission API", description = "게시글 관련 API")
@Validated
public interface SubmissionApi {

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 등록합니다. (썸네일, 분야 필수)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패 (제목, 썸네일 누락 등)", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody @Valid SubmissionCreateRequest request
    );

    @Operation(summary = "게시글 미리보기 조회", description = "게시글 ID를 통해 미리보기 정보를 조회합니다.")
    ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(
            @Parameter(description = "게시글 ID") @Positive Long submissionId
    );

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID와 페이지 번호를 통해 상세 정보를 조회합니다.")
    ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @Parameter(description = "게시글 ID") @Positive Long submissionId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") @Positive int page
    );

    @Operation(summary = "게시글 수정", description = "게시글의 제목, 내용, 태그, 이미지를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(hidden = true))),
    })
    ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @Parameter(description = "수정할 게시글 ID") @Positive Long submissionId,
            @RequestBody @Valid SubmissionUpdateRequest updateSubmission
    );

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(hidden = true))),
    })
    ResponseEntity<ApiResponse<Long>> deleteSubmission(
            @Parameter(description = "삭제할 게시글 ID") @Positive Long submissionId
    );
}