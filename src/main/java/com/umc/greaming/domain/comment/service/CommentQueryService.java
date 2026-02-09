package com.umc.greaming.domain.comment.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.dto.ReplyInfo;
import com.umc.greaming.domain.comment.dto.response.ReplyResponse;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.Reply;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.comment.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final S3Service s3Service;

    public ReplyResponse getReplyList(Long commentId, Long userId) { // userId: 현재 로그인한 사람(본인 확인용)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        List<Reply> replies = replyRepository.findAllByCommentOrderByCreatedAtAsc(comment);

        List<ReplyInfo> replyInfos = replies.stream().map(reply -> {

            String profileUrl = s3Service.getPublicUrl(reply.getUser().getProfileImageKey());

            boolean isWriter = (userId != null) && reply.getUser().getUserId().equals(userId);

            return ReplyInfo.from(reply, profileUrl, isWriter);
        }).toList();

        return ReplyResponse.of(replyInfos);
    }
}
