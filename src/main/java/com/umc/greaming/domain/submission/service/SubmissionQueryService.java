package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.challenge.repository.WeeklyUserScoreRepository;
import com.umc.greaming.domain.comment.dto.response.CommentInfo; // [추가]
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse; // [추가]
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.domain.submission.repository.SubmissionLikeRepository;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.tag.dto.TagInfo;
import com.umc.greaming.domain.tag.entity.SubmissionTag;
import com.umc.greaming.domain.tag.entity.Tag;
import com.umc.greaming.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private final TagRepository tagRepository;
    private static final int PAGE_SIZE = 30;

    private Submission findSubmissionByIdOrThrow(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
    }

    public SubmissionPreviewResponse getSubmissionPreview(Long submissionId) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);

        String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());

        return SubmissionPreviewResponse.from(submission, thumbnailUrl, tags);
    }

    // [수정] 상세 조회 로직 (댓글 변환 포함)
    public SubmissionDetailResponse getSubmissionDetail(Long submissionId, int page, User loginUser) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);

        // 1. 게시글 Info 생성
        SubmissionInfo submissionInfo = createSubmissionInfoFromEntity(submission, loginUser);

        // 2. 댓글 엔티티 페이징 조회
        List<TagInfo> tagInfos = getTagInfos(submissionId);
        SubmissionInfo submissionInfo = createSubmissionInfoFromEntity(submission, tagInfos);

        int pageIndex = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<Comment> commentEntityPage = commentRepository.findAllBySubmission(submission, pageable);

        // 3. [핵심] 댓글 엔티티 -> 댓글 Info 변환 (S3 URL 변환 수행)
        List<CommentInfo> commentInfos = commentEntityPage.getContent().stream()
                .map(comment -> {
                    // 작성자 프로필 이미지 URL 변환
                    String profileUrl = s3Service.getPublicUrl(comment.getUser().getProfileImageKey());

                    boolean isCommentLiked = false;

                    return CommentInfo.from(comment, profileUrl, isCommentLiked);
                })
                .toList();

        // 4. Page 메타데이터와 변환된 Info 리스트를 합침
        CommentPageResponse commentPageResponse = CommentPageResponse.from(commentEntityPage, commentInfos);

        // 5. 최종 응답 생성
        return SubmissionDetailResponse.from(submissionInfo, commentPageResponse);
    }

    public SubmissionInfo getSubmissionInfo(Long submissionId, User loginUser) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        return createSubmissionInfoFromEntity(submission, loginUser);
    }

    private SubmissionInfo createSubmissionInfoFromEntity(Submission submission, User loginUser) {
        Long submissionId = submission.getId();

        List<String> sortedImageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submissionId)
                .stream()
                .map(image -> s3Service.getPublicUrl(image.getImageKey()))
                .toList();

        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);

        String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());

        String level = weeklyUserScoreRepository.findByUserAndChallenge(submission.getUser(), submission.getChallenge())
                .map(score -> score.getJourneyLevel().name())
                .orElse(JourneyLevel.SKETCHER.name());

        boolean isLiked = false;
        if (loginUser != null) {
            isLiked = submissionLikeRepository.existsByUserAndSubmission(loginUser, submission);
        }

        return SubmissionInfo.from(submission, profileImageUrl, level, sortedImageUrls, tags, isLiked);
    }
    public CommentPageResponse getCommentList(Long submissionId, int page) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);

        int pageIndex = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<Comment> commentEntityPage = commentRepository.findAllBySubmission(submission, pageable);

        List<CommentInfo> commentInfos = commentEntityPage.getContent().stream()
                .map(comment -> {
                    String profileUrl = s3Service.getPublicUrl(comment.getUser().getProfileImageKey());
                    boolean isCommentLiked = false; // 좋아요 로직 필요 시 추가
                    return CommentInfo.from(comment, profileUrl, isCommentLiked);
                })
                .toList();

        return CommentPageResponse.from(commentEntityPage, commentInfos);
    }
}