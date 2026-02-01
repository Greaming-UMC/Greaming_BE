package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.service.SubmissionCommandService;
import com.umc.greaming.domain.submission.service.SubmissionQueryService;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/submissions")
public class SubmissionController implements SubmissionApi {

    private final SubmissionQueryService submissionQueryService;
    private final SubmissionCommandService submissionCommandService;

    @GetMapping("/{submissionId}/preview")
    public ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(@PathVariable Long submissionId) {
        SubmissionPreviewResponse result = submissionQueryService.getSubmissionPreview(submissionId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_PREVIEW_SUCCESS, result);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "1") int page
            //,@AuthenticationPrincipal UserDetails userDetails
    ) {
        SubmissionDetailResponse result = submissionQueryService.getSubmissionDetail(submissionId, page);
        return ApiResponse.success(SuccessStatus.SUBMISSION_DETAIL_SUCCESS, result);
    }

    @PutMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @PathVariable Long submissionId,
            @RequestBody SubmissionUpdateRequest updateSubmission
            //,@AuthenticationPrincipal UserDetails userDetails
            ) {
        SubmissionInfo result = submissionCommandService.updateSubmission(submissionId, updateSubmission);
        return ApiResponse.success(SuccessStatus.SUBMISSION_UPDATE, result);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<Long>> deleteSubmission(@PathVariable Long submissionId) {
        submissionCommandService.deleteSubmission(submissionId); //userId 추가필요
        return ApiResponse.success(SuccessStatus.SUBMISSION_DELETED, submissionId);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody SubmissionCreateRequest request
    ) {
        SubmissionInfo result = submissionCommandService.createSubmission(request);
        return ApiResponse.success(SuccessStatus.SUBMISSION_CREATED, result);
    }
}
