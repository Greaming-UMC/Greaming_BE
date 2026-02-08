package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.challenge.repository.WeeklyUserScoreRepository;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.comment.dto.ReplyInfo;
import com.umc.greaming.domain.comment.dto.response.ReplyResponse;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.Reply;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.comment.repository.ReplyRepository;
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
    private final CommentRepository commentRepository;
    private final SubmissionImageRepository submissionImageRepository;
    private final WeeklyUserScoreRepository weeklyUserScoreRepository;
    private final SubmissionLikeRepository submissionLikeRepository;
    private final S3Service s3Service;
    private final ReplyRepository replyRepository;

    private static final int PAGE_SIZE = 30;

    /**
     * 게시글 미리보기 조회 (단순 목록용)
     */
    public SubmissionPreviewResponse getSubmissionPreview(Long submissionId) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);
        String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());
        return SubmissionPreviewResponse.from(submission, thumbnailUrl, tags);
    }

    /**
     * 게시글 상세 조회 (게시글 정보 + 댓글 1페이지)
     */
    public SubmissionDetailResponse getSubmissionDetail(Long submissionId, int page, User loginUser) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);

        // 1. 게시글 Info 생성 (TagInfo 포함)
        SubmissionInfo submissionInfo = createSubmissionInfoFromEntity(submission, loginUser);

        // 2. 댓글 페이징 및 변환
        CommentPageResponse commentPageResponse = getCommentPageResponse(submission, page);

        return SubmissionDetailResponse.from(submissionInfo, commentPageResponse);
    }

    /**
     * 댓글 목록만 조회 (더보기/스크롤용)
     */
    public CommentPageResponse getCommentList(Long submissionId, int page) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        return getCommentPageResponse(submission, page);
    }

    /**
     * 게시글 단일 정보 조회 (수정 화면 등에서 사용)
     */
    public SubmissionInfo getSubmissionInfo(Long submissionId, User loginUser) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        return createSubmissionInfoFromEntity(submission, loginUser);
    }

    // --- [Private Helper Methods] ---

    private Submission findSubmissionByIdOrThrow(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
    }

    private CommentPageResponse getCommentPageResponse(Submission submission, int page) {
        int pageIndex = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<Comment> commentEntityPage = commentRepository.findAllBySubmission(submission, pageable);

        List<CommentInfo> commentInfos = commentEntityPage.getContent().stream()
                .map(comment -> {
                    String profileUrl = s3Service.getPublicUrl(comment.getUser().getProfileImageKey());
                    boolean isCommentLiked = false; // 댓글 좋아요 로직 필요 시 추가
                    return CommentInfo.from(comment, profileUrl, isCommentLiked);
                })
                .toList();

        return CommentPageResponse.from(commentEntityPage, commentInfos);
    }

    private SubmissionInfo createSubmissionInfoFromEntity(Submission submission, User loginUser) {
        Long submissionId = submission.getId();

        // 이미지 URL 리스트 변환
        List<String> sortedImageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submissionId)
                .stream()
                .map(image -> s3Service.getPublicUrl(image.getImageKey()))
                .toList();

        // [핵심 변경] Tag 엔티티 조회 후 TagInfo로 변환
        List<TagInfo> tagInfos = submissionTagRepository.findAllBySubmissionId(submissionId)
                .stream()
                .map(submissionTag -> TagInfo.from(submissionTag.getTag()))
                .toList();

        // 작성자 프로필 이미지
        String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());

        // 챌린지 레벨 (없으면 기본값)
        String level = JourneyLevel.SKETCHER.name();
        if (submission.getChallenge() != null) {
            level = weeklyUserScoreRepository.findByUserAndChallenge(submission.getUser(), submission.getChallenge())
                    .map(score -> score.getJourneyLevel().name())
                    .orElse(JourneyLevel.SKETCHER.name());
        }

        // 좋아요 여부 확인
        boolean isLiked = false;
        if (loginUser != null) {
            isLiked = submissionLikeRepository.existsByUserAndSubmission(loginUser, submission);
        }

        // DTO 반환 (List<TagInfo> 전달)
        return SubmissionInfo.from(submission, profileImageUrl, level, sortedImageUrls, tagInfos, isLiked);
    }

}