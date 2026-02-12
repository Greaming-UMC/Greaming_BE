package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.home.dto.response.HomeSubmissionsResponse;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionLikeResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.service.SubmissionCommandService;
import com.umc.greaming.domain.submission.service.SubmissionQueryService;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class SubmissionController implements SubmissionApi {

    private final SubmissionQueryService submissionQueryService;
    private final SubmissionCommandService submissionCommandService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<HomeSubmissionsResponse>> getAllSubmissions(
            int page,
            int size,
            String sortBy
    ) {
        HomeSubmissionsResponse result = submissionQueryService.getHomeSubmissions(page, size, sortBy);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DETAIL_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(
            @PathVariable Long submissionId
    ) {
        SubmissionPreviewResponse result = submissionQueryService.getSubmissionPreview(submissionId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_PREVIEW_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        SubmissionDetailResponse result = submissionQueryService.getSubmissionDetail(submissionId, page, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DETAIL_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody SubmissionCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        User user = findUserOrThrow(userId);
        SubmissionInfo result = submissionCommandService.createSubmission(request, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_CREATED, result);
    }

    @Override
    public ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @PathVariable Long submissionId,
            @RequestBody SubmissionUpdateRequest updateSubmission,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        User user = findUserOrThrow(userId);
        SubmissionInfo result = submissionCommandService.updateSubmission(submissionId, updateSubmission, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_UPDATE, result);
    }

    @Override
    public ResponseEntity<ApiResponse<Long>> deleteSubmission(
            @PathVariable Long submissionId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        User user = findUserOrThrow(userId);
        submissionCommandService.deleteSubmission(submissionId, user);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DELETED, submissionId);
    }

    @Override
    public ResponseEntity<ApiResponse<CommentPageResponse>> getCommentList(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        CommentPageResponse result = submissionQueryService.getCommentList(submissionId, page, userId);

        return ApiResponse.success(SuccessStatus.COMMENT_LIST_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<SubmissionLikeResponse>> toggleLike(
            @PathVariable Long submissionId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        User user = findUserOrThrow(userId);
        SubmissionLikeResponse result = submissionCommandService.toggleLike(submissionId, user);
        return ApiResponse.success(SuccessStatus.LIKE_TOGGLE_SUCCESS, result);
    }

    private User findUserOrThrow(Long userId) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}