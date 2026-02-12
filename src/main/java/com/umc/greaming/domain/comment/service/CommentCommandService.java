package com.umc.greaming.domain.comment.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.dto.ReplyInfo;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.Reply;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.comment.repository.ReplyRepository;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final SubmissionRepository submissionRepository;
    private final ReplyRepository replyRepository;
    private final S3Service s3Service;

    public CommentInfo createComment(CommentCreateRequest request, User user) {
        Submission submission = submissionRepository.findById(request.submissionId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
        
        Comment newComment = Comment.builder()
                .submission(submission)
                .user(user)
                .content(request.content())
                .build();

        Comment savedComment = commentRepository.save(newComment);

        submission.increaseCommentCount();

        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        return CommentInfo.from(savedComment, profileUrl, false, true);
    }

    public ReplyInfo createReply(Long commentId, ReplyCreateRequest request, User user) {

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        Reply newReply = Reply.builder()
                .comment(parentComment)
                .user(user)
                .content(request.content())
                .build();

        Reply savedReply = replyRepository.save(newReply);

        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        return ReplyInfo.from(savedReply, profileUrl, true);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus.COMMENT_NOT_AUTHORIZED);
        }

        comment.delete();

        comment.getSubmission().decreaseCommentCount();
    }
}