package com.umc.greaming.domain.challenge.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionCard;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionsResponse;
import com.umc.greaming.domain.challenge.entity.Challenge;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final S3Service s3Service;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    public ChallengeSubmissionsResponse getChallengeSubmissions(Long challengeId, int page, int size, String sortBy) {
        if (!challengeRepository.existsById(challengeId)) {
            throw new GeneralException(ErrorStatus.CHALLENGE_NOT_FOUND);
        }

        int validatedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int pageIndex = Math.max(0, page - 1);

        Page<Submission> submissionPage;

        if ("recommend".equals(sortBy)) {

            Pageable pageable = PageRequest.of(pageIndex, validatedSize);
            submissionPage = submissionRepository.findAllByChallengeIdOrderByRecommend(challengeId, pageable);
        } else {
            Sort sort = getSortCriteria(sortBy);
            Pageable pageable = PageRequest.of(pageIndex, validatedSize, sort);
            submissionPage = submissionRepository.findAllByChallengeId(challengeId, pageable);
        }

        List<ChallengeSubmissionCard> cards = submissionPage.getContent().stream()
                .map(submission -> {
                    String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());
                    return ChallengeSubmissionCard.from(submission, thumbnailUrl);
                })
                .toList();

        return ChallengeSubmissionsResponse.from(submissionPage, cards);
    }


    private Sort getSortCriteria(String sortBy) {
        return switch (sortBy) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "popular" -> Sort.by(Sort.Direction.DESC, "likeCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}