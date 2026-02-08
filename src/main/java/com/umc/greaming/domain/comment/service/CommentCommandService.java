package com.umc.greaming.domain.comment.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.Reply;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.comment.repository.ReplyRepository;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final SubmissionRepository submissionRepository;
    private final ReplyRepository replyRepository;

    public void createComment(CommentCreateRequest request, User user) {
        // 1. 게시글 존재 확인
        Submission submission = submissionRepository.findById(request.submissionId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        // 2. 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .submission(submission)
                .user(user)
                .content(request.content())
                .build();
        commentRepository.save(comment);
    }
    public void createReply(Long commentId, ReplyCreateRequest request, User user) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        Reply reply = Reply.builder()
                .comment(parentComment)
                .user(user)
                .content(request.content())
                .build();


        replyRepository.save(reply);
    }
}