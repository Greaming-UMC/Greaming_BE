package com.umc.greaming.domain.challenge.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionsResponse;
import com.umc.greaming.domain.challenge.service.ChallengeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Validated
public class ChallengeController implements ChallengeApi {

    private final ChallengeQueryService challengeQueryService;

    @Override
    public ResponseEntity<ApiResponse<ChallengeSubmissionsResponse>> getCurrentChallengeSubmissions(
            String challengeType,
            int page,
            int size
    ) {
        ChallengeSubmissionsResponse result = challengeQueryService.getCurrentChallengeSubmissions(
                challengeType, page, size
        );
        return ApiResponse.success(SuccessStatus.CHALLENGE_SUBMISSIONS_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<ChallengeSubmissionsResponse>> getChallengeSubmissionsByDate(
            String challengeType,
            LocalDateTime dateTime,
            int page,
            int size,
            String sortBy
    ) {
        ChallengeSubmissionsResponse result = challengeQueryService.getChallengeSubmissionsByDate(
                challengeType, dateTime, page, size, sortBy
        );
        return ApiResponse.success(SuccessStatus.CHALLENGE_SUBMISSIONS_SUCCESS, result);
    }
}
