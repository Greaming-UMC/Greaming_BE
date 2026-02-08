package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.service.SubmissionCommandService;
import com.umc.greaming.domain.submission.service.SubmissionQueryService;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository; // [추가] DB 조회용
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/submissions")
public class SubmissionController implements SubmissionApi { // Interface도 Long으로 맞춰줘야 함

    private final SubmissionQueryService submissionQueryService;
    private final SubmissionCommandService submissionCommandService;
    private final UserRepository userRepository; // [추가] 유저 조회용 레포지토리

    @Override
    @GetMapping("/{submissionId}/preview")
    public ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(@PathVariable Long submissionId) {
        SubmissionPreviewResponse result = submissionQueryService.getSubmissionPreview(submissionId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_PREVIEW_SUCCESS, result);
    }

    @Override
    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId // [수정] UserDetails -> Long
    ) {
        // 비로그인 유저도 조회 가능하므로 null 체크
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        SubmissionDetailResponse result = submissionQueryService.getSubmissionDetail(submissionId, page, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DETAIL_SUCCESS, result);
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody @Valid SubmissionCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId // [수정] Long으로 받음
    ) {
        // 로그인이 필수인 기능: 유저 없으면 에러 발생
        User user = findUserOrThrow(userId);
        SubmissionInfo result = submissionCommandService.createSubmission(request, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_CREATED, result);
    }

    @Override
    @PutMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @PathVariable Long submissionId,
            @RequestBody SubmissionUpdateRequest updateSubmission,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId // [수정]
    ) {
        User user = findUserOrThrow(userId);
        SubmissionInfo result = submissionCommandService.updateSubmission(submissionId, updateSubmission, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_UPDATE, result);
    }

    @Override
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<Long>> deleteSubmission(
            @PathVariable Long submissionId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId // [수정]
    ) {
        User user = findUserOrThrow(userId);
        submissionCommandService.deleteSubmission(submissionId, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DELETED, submissionId);
    }

    @Override
    @GetMapping("/{submissionId}/comments")
    public ResponseEntity<ApiResponse<CommentPageResponse>> getCommentList(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page
    ) {
        CommentPageResponse result = submissionQueryService.getCommentList(submissionId, page);
        return ApiResponse.success(SuccessStatus.COMMENT_LIST_SUCCESS, result);
    }

    // [편의 메서드] ID로 유저 찾기 (없으면 예외)
    private User findUserOrThrow(Long userId) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED); // 로그인 안 됨
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND)); // DB에 없음
    }
}