package com.umc.greaming.domain.comment.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.dto.ReplyInfo;
import com.umc.greaming.domain.comment.dto.response.ReplyResponse;
import com.umc.greaming.domain.comment.service.CommentCommandService;
import com.umc.greaming.domain.comment.service.CommentQueryService;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<ReplyResponse>> getReplyList(Long commentId, Long userId) {
        ReplyResponse result = commentQueryService.getReplyList(commentId, userId);
        return ApiResponse.success(SuccessStatus.COMMENT_LIST_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<CommentInfo>> createComment(
            CommentCreateRequest request,
            Long userId
    ) {
        User user = findUserOrThrow(userId);

        CommentInfo result = commentCommandService.createComment(request, user);

        return ApiResponse.success(SuccessStatus.COMMENT_CREATED, result);
    }

    @Override
    public ResponseEntity<ApiResponse<ReplyInfo>> createReply(
            Long commentId,
            ReplyCreateRequest request,
            Long userId
    ) {
        User user = findUserOrThrow(userId);

        ReplyInfo result = commentCommandService.createReply(commentId, request, user);

        return ApiResponse.success(SuccessStatus.COMMENT_CREATED, result);
    }

    private User findUserOrThrow(Long userId) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}