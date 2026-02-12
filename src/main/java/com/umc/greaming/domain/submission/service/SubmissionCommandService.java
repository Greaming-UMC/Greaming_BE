package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.challenge.repository.WeeklyUserScoreRepository;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import com.umc.greaming.domain.tag.dto.TagInfo;
import com.umc.greaming.domain.submission.entity.SubmissionTag;
import com.umc.greaming.domain.tag.entity.Tag;
import com.umc.greaming.domain.tag.repository.TagRepository;
import com.umc.greaming.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionCommandService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionImageRepository submissionImageRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final TagRepository tagRepository;
    private final WeeklyUserScoreRepository weeklyUserScoreRepository;
    private final S3Service s3Service;

    public SubmissionInfo createSubmission(SubmissionCreateRequest request, User user) {

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

        saveImages(submission, request.imageList());

        saveTags(submission, request.tags());

        return createSubmissionInfo(submission, user);
    }

    public SubmissionInfo updateSubmission(Long submissionId, SubmissionUpdateRequest request, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        if (!submission.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }

        submission.updateInfo(request.title(), request.caption());
        submission.updateVisibility(request.visibility());
        submission.changeCommentEnabled(request.commentEnabled());

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
        List<String> imageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submission.getId())
                .stream()
                .map(img -> s3Service.getPublicUrl(img.getImageKey()))
                .toList();

        List<TagInfo> tagInfos = submissionTagRepository.findAllBySubmissionId(submission.getId())
                .stream()
                .map(st -> TagInfo.from(st.getTag()))
                .toList();

        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        String level = getLevelBySubmission(submission);

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