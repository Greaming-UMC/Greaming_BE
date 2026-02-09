package com.umc.greaming.domain.user.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import com.umc.greaming.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @GetMapping("/check-registered")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkRegistered(
            @AuthenticationPrincipal Long userId
    ) {
        boolean registered = userService.isProfileRegistered(userId);
        return ApiResponse.success(
                SuccessStatus.USER_CHECK_REGISTERED_SUCCESS,
                Map.of("profileRegistered", registered)
        );
    }

    @Override
    @PostMapping("/registinfo")
    public ResponseEntity<ApiResponse<Void>> registInfo(
            @AuthenticationPrincipal Long userId,
            @RequestBody RegistInfoRequest request
    ) {
        userService.registInfo(userId, request);
        return ApiResponse.success(SuccessStatus.USER_REGIST_INFO_SUCCESS);
    }
}