package com.umc.greaming.domain.follow.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.follow.dto.FollowListResponse;
import com.umc.greaming.domain.follow.dto.FollowResponse;
import com.umc.greaming.domain.follow.service.FollowCommandService;
import com.umc.greaming.domain.follow.service.FollowQueryService;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController implements FollowApi {

    private final FollowCommandService followCommandService;
    private final FollowQueryService followQueryService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<FollowResponse>> toggleFollow(
            @PathVariable Long targetUserId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        User me = findUserOrThrow(userId);
        FollowResponse result = followCommandService.toggleFollow(targetUserId, me);
        return ApiResponse.success(SuccessStatus.FOLLOW_TOGGLE_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<FollowListResponse>> getFollowings(
            @PathVariable Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long loginUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        FollowListResponse result = followQueryService.getFollowings(userId, loginUserId, page, size);
        return ApiResponse.success(SuccessStatus.FOLLOW_LIST_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<FollowListResponse>> getFollowers(
            @PathVariable Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long loginUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        FollowListResponse result = followQueryService.getFollowers(userId, loginUserId, page, size);
        return ApiResponse.success(SuccessStatus.FOLLOW_LIST_SUCCESS, result);
    }

    private User findUserOrThrow(Long userId) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}
