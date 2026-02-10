package com.umc.greaming.domain.challenge.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionCard;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionsResponse;
import com.umc.greaming.domain.challenge.entity.Challenge;
import com.umc.greaming.domain.challenge.enums.Cycle;
import com.umc.greaming.domain.challenge.repository.ChallengeRepository;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final S3Service s3Service;

    private static final int MAX_PAGE_SIZE = 50;

    public ChallengeSubmissionsResponse getCurrentChallengeSubmissions(String challengeType, int page, int size) {
        Cycle cycle;
        try {
            cycle = Cycle.valueOf(challengeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        Challenge challenge = challengeRepository.findCurrentChallenge(cycle, now)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHALLENGE_NOT_FOUND));

        int validatedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, validatedSize, sort);

        Page<Submission> submissionPage = submissionRepository.findAllByChallengeId(challenge.getId(), pageable);

        List<ChallengeSubmissionCard> cards = submissionPage.getContent().stream()
                .map(submission -> {
                    String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());
                    String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());
                    return ChallengeSubmissionCard.from(submission, thumbnailUrl, profileImageUrl);
                })
                .toList();

        return ChallengeSubmissionsResponse.from(submissionPage, cards);
    }

    public ChallengeSubmissionsResponse getChallengeSubmissionsByDate(
            String challengeType,
            LocalDateTime dateTime,
            int page,
            int size,
            String sortBy
    ) {

        Cycle cycle;
        try {
            cycle = Cycle.valueOf(challengeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        Challenge challenge = challengeRepository.findByCycleAndDate(cycle, dateTime)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHALLENGE_NOT_FOUND));

        int validatedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int pageIndex = Math.max(0, page - 1);
        Page<Submission> submissionPage;

        if ("recommend".equals(sortBy)) {
            Pageable pageable = PageRequest.of(pageIndex, validatedSize);
            submissionPage = submissionRepository.findAllByChallengeIdOrderByRecommend(challenge.getId(), pageable);
        } else {
            Sort sort = getSortCriteria(sortBy);
            Pageable pageable = PageRequest.of(pageIndex, validatedSize, sort);
            submissionPage = submissionRepository.findAllByChallengeId(challenge.getId(), pageable);
        }

        List<ChallengeSubmissionCard> cards = submissionPage.getContent().stream()
                .map(submission -> {
                    String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());
                    String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());
                    return ChallengeSubmissionCard.from(submission, thumbnailUrl, profileImageUrl);
                })
                .toList();

        return ChallengeSubmissionsResponse.from(submissionPage, cards);
    }

    @Deprecated
    private Sort getSortCriteria(String sortBy) {
        return switch (sortBy) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "popular" -> Sort.by(Sort.Direction.DESC, "likeCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}