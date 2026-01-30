package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionQueryService submissionService;

    @GetMapping("/{submissionId}/preview")
    public ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(@PathVariable @Positive Long submissionId) {
        SubmissionPreviewResponse result = submissionService.getSubmissionPreview(submissionId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_PREVIEW_SUCCESS, result);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @PathVariable @Positive Long submissionId,
            @RequestParam(defaultValue = "1") @Positive int page
    ) {
        SubmissionDetailResponse result = submissionService.getSubmissionDetail(submissionId, page);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DETAIL_SUCCESS, result);
    }

    @PutMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @PathVariable @Positive Long submissionId,
            @RequestBody SubmissionUpdateRequest updateSubmission
    ) {
        Long userId = 1L;
        SubmissionInfo result = submissionService.updateSubmission(submissionId, updateSubmission, userId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_UPDATE, result);
    }
}