package com.umc.greaming.domain.home.controller;

import com.umc.greaming.common.response.ApiResponse; // [수정] 패키지 경로 확인
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.home.dto.response.HomeResponse;
import com.umc.greaming.domain.home.service.HomeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Tag(name = "Home API", description = "홈 화면 관련 API")
public class HomeController {

    private final HomeQueryService homeQueryService;

    @Operation(summary = "메인화면 상단 조회", description = "데일리/주간 챌린지 정보와 사용자 요약 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        HomeResponse result = homeQueryService.getHomeData(userId);

        return ApiResponse.success(SuccessStatus.HOME_SUCCESS, result);
    }
}