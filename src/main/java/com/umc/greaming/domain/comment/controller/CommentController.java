package com.umc.greaming.domain.comment.controller;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.response.ReplyResponse;
import com.umc.greaming.domain.comment.service.CommentCommandService;
import com.umc.greaming.domain.comment.service.CommentQueryService;
import com.umc.greaming.domain.submission.service.SubmissionQueryService; // 서비스 위치 확인
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final SubmissionQueryService submissionQueryService; // 답글 조회용
    private final CommentCommandService commentCommandService; // [추가] 댓글 생성용
    private final UserRepository userRepository; // 유저 조회용
    private final CommentQueryService commentQueryService;
    @Override
    public ResponseEntity<ApiResponse<ReplyResponse>> getReplyList(Long commentId, Long userId) {
        ReplyResponse result = commentQueryService.getReplyList(commentId, userId);
        return ApiResponse.success(SuccessStatus.COMMENT_LIST_SUCCESS, result);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> createComment(
            CommentCreateRequest request,
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        commentCommandService.createComment(request, user);
        return ApiResponse.success(SuccessStatus.COMMENT_CREATED, "댓글이 등록되었습니다.");
    }
    @Override
    public ResponseEntity<ApiResponse<String>> createReply(
            Long commentId,
            ReplyCreateRequest request,
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        commentCommandService.createReply(commentId, request, user);

        return ApiResponse.success(SuccessStatus.COMMENT_CREATED, "답글이 등록되었습니다.");
    }
}