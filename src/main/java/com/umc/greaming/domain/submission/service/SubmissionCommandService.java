package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.challenge.repository.WeeklyUserScoreRepository;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionLikeResponse;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import com.umc.greaming.domain.submission.entity.SubmissionLike;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionLikeRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import com.umc.greaming.domain.tag.dto.TagInfo;
import com.umc.greaming.domain.submission.entity.SubmissionTag;
import com.umc.greaming.domain.tag.entity.Tag;
import com.umc.greaming.domain.tag.repository.TagRepository;
import com.umc.greaming.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SubmissionCommandService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionImageRepository submissionImageRepository;
    private final SubmissionLikeRepository submissionLikeRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final TagRepository tagRepository;
    private final WeeklyUserScoreRepository weeklyUserScoreRepository;
    private final S3Service s3Service;

    public SubmissionInfo createSubmission(SubmissionCreateRequest request, User user) {

        log.info("=== createSubmission 시작 ===");
        log.info("User: {}", user != null ? user.getUserId() : "NULL");
        log.info("ProfileImageKey: {}", user != null ? user.getProfileImageKey() : "NULL");
        log.info("Request - title: {}, thumbnailKey: {}", request.title(), request.thumbnailKey());

        Submission submission = Submission.builder()
                .user(user)
                .title(request.title())
                .caption(request.caption())
                .visibility(request.visibility())
                .commentEnabled(request.commentEnabled())
                .field(request.field())
                .thumbnailKey(request.thumbnailKey())
                .build();

        submission = submissionRepository.saveAndFlush(submission);
        log.info("Submission 저장 완료 - ID: {}", submission.getId());

        saveImages(submission, request.imageList());

        saveTags(submission, request.tags());
        
        log.info("이미지/태그 저장 완료, createSubmissionInfo 호출");

        return createSubmissionInfo(submission, user);
    }

    public SubmissionInfo updateSubmission(Long submissionId, SubmissionUpdateRequest request, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        if (!submission.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }

        submission.update(request.title(), request.caption(), request.visibility(), request.commentEnabled(), request.thumbnailKey());

        if (request.imageList() != null) {
            submissionImageRepository.deleteAllBySubmission(submission);
            submissionImageRepository.flush();
            saveImages(submission, request.imageList());
        }

        if (request.tags() != null) {
            submissionTagRepository.deleteAllBySubmission(submission);
            submissionTagRepository.flush();
            saveTags(submission, request.tags());
        }

        return createSubmissionInfo(submission, user);
    }


    public void deleteSubmission(Long submissionId, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        if (!submission.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }
        submission.delete();
    }

    public SubmissionLikeResponse toggleLike(Long submissionId, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        boolean exists = submissionLikeRepository.existsByUserAndSubmission(user, submission);
        boolean isLiked;

        if (exists) {
            submissionLikeRepository.deleteByUserAndSubmission(user, submission);
            submission.decreaseLikeCount();
            isLiked = false;
            log.info("좋아요 취소 - Submission ID: {}, User ID: {}", submissionId, user.getUserId());
        } else {
            SubmissionLike submissionLike = SubmissionLike.builder()
                    .user(user)
                    .submission(submission)
                    .build();
            submissionLikeRepository.save(submissionLike);
            submission.increaseLikeCount();
            isLiked = true;
            log.info("좋아요 추가 - Submission ID: {}, User ID: {}", submissionId, user.getUserId());
        }

        return SubmissionLikeResponse.of(isLiked, submission.getLikeCount());
    }


    private void saveImages(Submission submission, List<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) return;

        List<SubmissionImage> images = IntStream.range(0, imageKeys.size())
                .mapToObj(i -> SubmissionImage.builder()
                        .submission(submission)
                        .imageKey(imageKeys.get(i))
                        .sortOrder(i)
                        .build())
                .toList();
        submissionImageRepository.saveAll(images);
    }

    private void saveTags(Submission submission, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return;

        for (String tagName : tagNames) {
            String trimmedTagName = tagName.trim();
            if (trimmedTagName.isEmpty()) continue;

            Tag tag = tagRepository.findByName(trimmedTagName)
                    .orElseGet(() -> {
                        Tag newTag = Tag.builder().name(trimmedTagName).build();
                        return tagRepository.save(newTag);
                    });

            SubmissionTag submissionTag = SubmissionTag.builder()
                    .submission(submission)
                    .tag(tag)
                    .build();
            submissionTagRepository.save(submissionTag);
        }
    }

    private SubmissionInfo createSubmissionInfo(Submission submission, User user) {
        log.info("=== createSubmissionInfo 시작 ===");
        log.info("Submission ID: {}", submission.getId());
        log.info("User ID: {}", user.getUserId());
        log.info("User ProfileImageKey: {}", user.getProfileImageKey());
        
        List<String> imageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submission.getId())
                .stream()
                .map(img -> s3Service.getPublicUrl(img.getImageKey()))
                .toList();
        log.info("이미지 URL 생성 완료: {} 개", imageUrls.size());

        List<TagInfo> tagInfos = submissionTagRepository.findAllBySubmissionId(submission.getId())
                .stream()
                .map(st -> TagInfo.from(st.getTag()))
                .toList();
        log.info("태그 정보 생성 완료: {} 개", tagInfos.size());

        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());
        log.info("프로필 URL 생성: {}", profileUrl);

        String level = getLevelBySubmission(submission);
        log.info("레벨 조회 완료: {}", level);

        return SubmissionInfo.from(submission, profileUrl, level, imageUrls, tagInfos, false);
    }

    private String getLevelBySubmission(Submission submission) {
        if (submission.getChallenge() == null) {
            return JourneyLevel.SKETCHER.name();
        }
        return weeklyUserScoreRepository.findByUserAndChallenge(submission.getUser(), submission.getChallenge())
                .map(score -> score.getJourneyLevel().name())
                .orElse(JourneyLevel.SKETCHER.name());
    }
}