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
import com.umc.greaming.domain.tag.entity.SubmissionTag;
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

    /**
     * 게시글 생성
     */
    public SubmissionInfo createSubmission(SubmissionCreateRequest request, User user) {

        // 1. 게시글 엔티티 생성 및 저장 (Builder 사용)
        Submission submission = Submission.builder()
                .user(user)
                .title(request.title())
                .caption(request.caption())
                .visibility(request.visibility())
                .commentEnabled(request.commentEnabled())
                .field(request.field())
                .thumbnailKey(request.thumbnailKey())
                // .challenge(...) // 챌린지 연동 로직 필요 시 추가
                .build();

        submissionRepository.save(submission);

        // 2. 이미지 저장
        saveImages(submission, request.imageList());

        // 3. 태그 저장
        saveTags(submission, request.tags());

        // 4. 응답 DTO 생성
        return createSubmissionInfo(submission, user);
    }

    /**
     * 게시글 수정
     */
    public SubmissionInfo updateSubmission(Long submissionId, SubmissionUpdateRequest request, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        // 1. 권한 검증 (User PK: userId)
        if (!submission.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }

        // 2. 엔티티 비즈니스 메서드를 통한 정보 수정 (Setter 사용 X)
        // 제목, 내용 수정 (null 체크는 엔티티 내부 or 메서드 호출 전 수행)
        submission.updateInfo(request.title(), request.caption());

        // 공개 범위 수정
        submission.updateVisibility(request.visibility());

        // 댓글 허용 여부 수정
        submission.changeCommentEnabled(request.commentEnabled());

        // 3. 이미지 수정 (기존 이미지 삭제 -> 새 이미지 저장)
        if (request.imageList() != null) {
            submissionImageRepository.deleteAllBySubmission(submission);
            saveImages(submission, request.imageList());
        }

        // 4. 태그 수정 (기존 태그 삭제 -> 새 태그 저장)
        if (request.tags() != null) {
            submissionTagRepository.deleteAllBySubmission(submission);
            saveTags(submission, request.tags());
        }

        return createSubmissionInfo(submission, user);
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    public void deleteSubmission(Long submissionId, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        // 권한 검증
        if (!submission.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }

        // Soft Delete 적용
        // 엔티티의 delete() 메서드가 deletedAt을 현재 시간으로 설정함.
        // @Transactional에 의해 메서드 종료 시 Dirty Checking으로 DB에 반영됨.
        submission.delete();
    }


    // --- [내부 헬퍼 메서드] ---

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
            // 태그가 이미 존재하면 조회, 없으면 생성 (중복 방지)
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

            SubmissionTag submissionTag = SubmissionTag.builder()
                    .submission(submission)
                    .tag(tag)
                    .build();
            submissionTagRepository.save(submissionTag);
        }
    }

    private SubmissionInfo createSubmissionInfo(Submission submission, User user) {
        // 이미지 URL 변환
        List<String> imageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submission.getId())
                .stream()
                .map(img -> s3Service.getPublicUrl(img.getImageKey()))
                .toList();

        // 태그 이름 리스트 조회
        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submission.getId());

        // 프로필 이미지 URL
        String profileUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        // 레벨 조회 (없으면 SKETCHER)
        String level = getLevelBySubmission(submission);

        // 작성/수정 직후이므로 좋아요 여부는 false
        return SubmissionInfo.from(submission, profileUrl, level, imageUrls, tags, false);
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