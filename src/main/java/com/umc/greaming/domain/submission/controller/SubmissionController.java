package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.service.SubmissionCommandService;
import com.umc.greaming.domain.submission.service.SubmissionQueryService;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.user.entity.User;
// import com.umc.greaming.global.security.UserDetailsImpl; // ★ UserDetails 구현체 경로 import 확인

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter; // [추가] Swagger 설정용
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/submissions")
public class SubmissionController implements SubmissionApi {

    private final SubmissionQueryService submissionQueryService;
    private final SubmissionCommandService submissionCommandService;

    @GetMapping("/{submissionId}/preview")
    @Operation(summary = "게시글 미리보기 조회", description = "로그인 없이 게시글의 썸네일과 태그 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(@PathVariable Long submissionId) {
        SubmissionPreviewResponse result = submissionQueryService.getSubmissionPreview(submissionId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_PREVIEW_SUCCESS, result);
    }

    @GetMapping("/{submissionId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 내용과 댓글을 조회합니다. (로그인 시 좋아요 여부 포함)")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = (userDetails != null) ? ((UserDetailsImpl) userDetails).getUser() : null;
        SubmissionDetailResponse result = submissionQueryService.getSubmissionDetail(submissionId, page, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DETAIL_SUCCESS, result);
    }

    @PostMapping
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 업로드합니다.")
    public ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody @Valid SubmissionCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        SubmissionInfo result = submissionCommandService.createSubmission(request, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_CREATED, result);
    }

    @PutMapping("/{submissionId}")
    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    public ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @PathVariable Long submissionId,
            @RequestBody SubmissionUpdateRequest updateSubmission,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        SubmissionInfo result = submissionCommandService.updateSubmission(submissionId, updateSubmission, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_UPDATE, result);
    }

    @DeleteMapping("/{submissionId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<ApiResponse<Long>> deleteSubmission(
            @PathVariable Long submissionId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        submissionCommandService.deleteSubmission(submissionId, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DELETED, submissionId);
    }
    @GetMapping("/{submissionId}/comments")
    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글만 페이징하여 조회합니다. (2페이지부터 사용 권장)")
    public ResponseEntity<ApiResponse<CommentPageResponse>> getCommentList(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page
    ) {
        CommentPageResponse result = submissionQueryService.getCommentList(submissionId, page);
        return ApiResponse.success(SuccessStatus.COMMENT_LIST_SUCCESS, result);
    }
}