package com.umc.greaming.domain.challenge.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionsResponse;
import com.umc.greaming.domain.challenge.service.ChallengeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class ChallengeController implements ChallengeApi {

    private final ChallengeQueryService challengeQueryService;

    @Override
    public ResponseEntity<ApiResponse<ChallengeSubmissionsResponse>> getChallengeSubmissions(
            Long challengeId,
            int page,
            int size,
            String sortBy
    ) {
        ChallengeSubmissionsResponse result = challengeQueryService.getChallengeSubmissions(
                challengeId, page, size, sortBy
        );
        return ApiResponse.success(SuccessStatus.CHALLENGE_SUBMISSIONS_SUCCESS, result);
    }
}
