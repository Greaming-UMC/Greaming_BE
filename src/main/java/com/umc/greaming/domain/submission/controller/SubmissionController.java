package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/works")
public class SubmissionController {

    private final SubmissionQueryService submissionQueryService;

    @GetMapping("/{workId}/preview")
    public ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(@PathVariable @Positive Long workId) {
        SubmissionPreviewResponse result = submissionQueryService.getSubmissionPreview(workId);
        return ApiResponse.success(SuccessStatus.SUBMISSION_PREVIEW_SUCCESS, result);
    }
}
