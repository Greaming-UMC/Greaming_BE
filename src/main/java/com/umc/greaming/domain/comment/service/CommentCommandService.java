package com.umc.greaming.domain.comment.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service; // [추가]
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.dto.request.CommentCreateRequest;
import com.umc.greaming.domain.comment.dto.request.ReplyCreateRequest;
import com.umc.greaming.domain.comment.dto.CommentInfo; // [추가]
import com.umc.greaming.domain.comment.dto.ReplyInfo; // [추가]
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

    // ▼▼▼ [추가] 프로필 이미지 URL 변환을 위해 필요 ▼▼▼
    private final S3Service s3Service;

    /**
     * 댓글 생성
     * @return 생성된 댓글 정보 (CommentInfo)
     */
    public CommentInfo createComment(CommentCreateRequest request, User user) {
        // 1. 게시글 존재 확인
        Submission submission = submissionRepository.findById(request.submissionId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        // 2. 댓글 엔티티 생성 및 저장
        Comment newComment = Comment.builder()
                .submission(submission)
                .user(user)
                .content(request.content())
                .build();

        Comment savedComment = commentRepository.save(newComment);

        // 3. [추가] DTO 변환 및 반환
        // 방금 만든 댓글이므로 좋아요(false), 작성자 본인(true)
        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        return CommentInfo.from(savedComment, profileUrl, false, true);
    }

    /**
     * 답글 생성
     * @return 생성된 답글 정보 (ReplyInfo)
     */
    public ReplyInfo createReply(Long commentId, ReplyCreateRequest request, User user) {
        // 1. 부모 댓글 존재 확인
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        // 2. 답글 엔티티 생성 및 저장
        Reply newReply = Reply.builder()
                .comment(parentComment)
                .user(user)
                .content(request.content())
                .build();

        Reply savedReply = replyRepository.save(newReply);

        // 3. [추가] DTO 변환 및 반환
        // 방금 만든 답글이므로 작성자 본인(true)
        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        return ReplyInfo.from(savedReply, profileUrl, true);
    }
}