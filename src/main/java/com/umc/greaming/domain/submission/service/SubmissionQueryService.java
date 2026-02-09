package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.challenge.repository.WeeklyUserScoreRepository;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.repository.CommentLikeRepository;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionLikeRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import com.umc.greaming.domain.tag.dto.TagInfo;
import com.umc.greaming.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionQueryService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionImageRepository submissionImageRepository;
    private final SubmissionLikeRepository submissionLikeRepository;

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final WeeklyUserScoreRepository weeklyUserScoreRepository;
    private final S3Service s3Service;

    private static final int PAGE_SIZE = 30;

    public SubmissionPreviewResponse getSubmissionPreview(Long submissionId) {

        Submission submission = submissionRepository.findByIdWithUser(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);
        String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());
        return SubmissionPreviewResponse.from(submission, thumbnailUrl, tags);
    }

    public SubmissionDetailResponse getSubmissionDetail(Long submissionId, int page, User loginUser) {

        Submission submission = submissionRepository.findByIdWithUserAndChallenge(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
        SubmissionInfo submissionInfo = createSubmissionInfoFromEntity(submission, loginUser);

        Long userId = (loginUser != null) ? loginUser.getUserId() : null;
        CommentPageResponse commentPageResponse = getCommentPageResponse(submission, page, userId);

        return SubmissionDetailResponse.from(submissionInfo, commentPageResponse);
    }

    public CommentPageResponse getCommentList(Long submissionId, int page, Long userId) {

        Submission submission = findSubmissionByIdOrThrow(submissionId);
        return getCommentPageResponse(submission, page, userId);
    }

    public SubmissionInfo getSubmissionInfo(Long submissionId, User loginUser) {

        Submission submission = submissionRepository.findByIdWithUserAndChallenge(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
        return createSubmissionInfoFromEntity(submission, loginUser);
    }

    private Submission findSubmissionByIdOrThrow(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
    }

    private CommentPageResponse getCommentPageResponse(Submission submission, int page, Long userId) {
        int pageIndex = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Comment> commentEntityPage = commentRepository.findAllBySubmission(submission, pageable);

        List<Long> likedCommentIds = List.of();
        if (userId != null && !commentEntityPage.isEmpty()) {
            List<Long> commentIds = commentEntityPage.getContent().stream()
                    .map(Comment::getId)
                    .toList();
            likedCommentIds = commentLikeRepository.findLikedCommentIdsByUserIdAndCommentIds(userId, commentIds);
        }
        
        final List<Long> finalLikedCommentIds = likedCommentIds;

        List<CommentInfo> commentInfos = commentEntityPage.getContent().stream()
                .map(comment -> {
                    String profileUrl = s3Service.getPublicUrl(comment.getUser().getProfileImageKey());

                    boolean isCommentLiked = finalLikedCommentIds.contains(comment.getId());

                    boolean isWriter = (userId != null) && comment.getUser().getUserId().equals(userId);

                    return CommentInfo.from(comment, profileUrl, isCommentLiked, isWriter);
                })
                .toList();

        return CommentPageResponse.from(commentEntityPage, commentInfos);
    }

    private SubmissionInfo createSubmissionInfoFromEntity(Submission submission, User loginUser) {
        Long submissionId = submission.getId();

        List<String> sortedImageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submissionId)
                .stream()
                .map(image -> s3Service.getPublicUrl(image.getImageKey()))
                .toList();

        List<TagInfo> tagInfos = submissionTagRepository.findAllBySubmissionId(submissionId)
                .stream()
                .map(submissionTag -> TagInfo.from(submissionTag.getTag()))
                .toList();

        String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());

        String level = JourneyLevel.SKETCHER.name();
        if (submission.getChallenge() != null) {
            level = weeklyUserScoreRepository.findByUserAndChallenge(submission.getUser(), submission.getChallenge())
                    .map(score -> score.getJourneyLevel().name())
                    .orElse(JourneyLevel.SKETCHER.name());
        }

        boolean isLiked = false;
        if (loginUser != null) {
            isLiked = submissionLikeRepository.existsByUserAndSubmission(loginUser, submission);
        }

        return SubmissionInfo.from(submission, profileImageUrl, level, sortedImageUrls, tagInfos, isLiked);
    }
}