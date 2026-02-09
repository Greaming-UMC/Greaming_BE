package com.umc.greaming.domain.comment.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
// ▼▼▼ [수정 포인트 1] 패키지 경로 확인 (response 패키지인지 dto 패키지인지 확인 후 통일)
// CommentApi 인터페이스에 선언된 경로와 100% 일치해야 합니다.
import com.umc.greaming.domain.comment.dto.CommentInfo;
// 만약 CommentInfo가 'dto.response' 패키지에 있다면 아래 주석 해제 후 위 라인 삭제
// import com.umc.greaming.domain.comment.dto.response.CommentInfo;

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

        // [수정 포인트 2] Service가 void가 아닌 CommentInfo를 반환하는지 확인 필수!
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

        // [수정 포인트 3] Service가 void가 아닌 ReplyInfo를 반환하는지 확인 필수!
        ReplyInfo result = commentCommandService.createReply(commentId, request, user);

        return ApiResponse.success(SuccessStatus.COMMENT_CREATED, result);
    }

    // --- Helper Method ---
    private User findUserOrThrow(Long userId) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}