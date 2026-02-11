package com.umc.greaming.domain.user.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import com.umc.greaming.domain.user.dto.request.UpdateUserInfoRequest;
import com.umc.greaming.domain.user.dto.response.UserInfoResponse;
import com.umc.greaming.domain.user.dto.response.UserSearchResponse;
import com.umc.greaming.domain.user.service.UserQueryService;
import com.umc.greaming.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final UserQueryService userQueryService;

    @Override
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
    public ResponseEntity<ApiResponse<Void>> registInfo(
            @AuthenticationPrincipal Long userId,
            @RequestBody RegistInfoRequest request
    ) {
        userService.registInfo(userId, request);
        return ApiResponse.success(SuccessStatus.USER_REGIST_INFO_SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateInfo(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid UpdateUserInfoRequest request
    ) {
        userService.updateInfo(userId, request);
        return ApiResponse.success(SuccessStatus.USER_UPDATE_INFO_SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @PathVariable Long userId
    ) {
        UserInfoResponse response = userQueryService.getUserInfo(userId);
        return ApiResponse.success(SuccessStatus.USER_GET_INFO_SUCCESS, response);
    }

    @Override
    public ResponseEntity<ApiResponse<UserSearchResponse>> searchByNickname(
            @AuthenticationPrincipal Long userId,
            @RequestParam String nickname
    ) {
        UserSearchResponse response = userQueryService.searchByNickname(nickname);
        return ApiResponse.success(SuccessStatus.USER_SEARCH_SUCCESS, response);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkIsMe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long targetUserId
    ) {
        boolean isMe = userId.equals(targetUserId);
        return ApiResponse.success(SuccessStatus.USER_CHECK_IS_ME_SUCCESS, Map.of("isMe", isMe));
    }

    @Override
    public ResponseEntity<ApiResponse<com.umc.greaming.domain.user.dto.response.MyProfileTopResponse>> getMyProfileTop(
            @AuthenticationPrincipal Long userId
    ) {
        com.umc.greaming.domain.user.dto.response.MyProfileTopResponse response = userQueryService.getMyProfileTop(userId);
        return ApiResponse.success(SuccessStatus.USER_GET_PROFILE_TOP_SUCCESS, response);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkNickname(
            @RequestParam String nickname
    ) {
        boolean isAvailable = userQueryService.checkNicknameAvailability(nickname);
        return ApiResponse.success(
                SuccessStatus.USER_CHECK_NICKNAME_SUCCESS,
                Map.of("isAvailable", isAvailable)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<com.umc.greaming.domain.user.dto.response.MyProfileSettingsResponse>> getMyProfileSettings(
            @AuthenticationPrincipal Long userId
    ) {
        com.umc.greaming.domain.user.dto.response.MyProfileSettingsResponse response = userQueryService.getMyProfileSettings(userId);
        return ApiResponse.success(SuccessStatus.USER_GET_PROFILE_SETTINGS_SUCCESS, response);
    }
}
