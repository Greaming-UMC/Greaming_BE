package com.umc.greaming.domain.comment.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.dto.ReplyInfo;
import com.umc.greaming.domain.comment.dto.response.CommentLikeResponse;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.CommentLike;
import com.umc.greaming.domain.comment.entity.Reply;
import com.umc.greaming.domain.comment.repository.CommentLikeRepository;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.comment.repository.ReplyRepository;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final SubmissionRepository submissionRepository;
    private final ReplyRepository replyRepository;
    private final CommentLikeRepository commentLikeRepository;
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

    public CommentLikeResponse toggleLike(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        boolean exists = commentLikeRepository.existsByUserAndComment(user, comment);
        boolean isLiked;

        if (exists) {
            commentLikeRepository.deleteByUserAndComment(user, comment);
            comment.decreaseLikeCount();
            isLiked = false;
            log.info("댓글 좋아요 취소 - Comment ID: {}, User ID: {}", commentId, user.getUserId());
        } else {
            CommentLike commentLike = CommentLike.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
            comment.increaseLikeCount();
            isLiked = true;
            log.info("댓글 좋아요 추가 - Comment ID: {}, User ID: {}", commentId, user.getUserId());
        }

        return CommentLikeResponse.of(isLiked, comment.getLikeCount());
    }
}