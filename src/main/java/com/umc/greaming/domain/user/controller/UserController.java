package com.umc.greaming.domain.user.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.user.dto.response.MyProfileTopResponse;
import com.umc.greaming.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyProfileTopResponse>> getMyProfileTop(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        MyProfileTopResponse result = userQueryService.getMyProfileTop(userId);
        return ApiResponse.success(SuccessStatus.USER_PROFILE_TOP_SUCCESS, result);
    }
}
