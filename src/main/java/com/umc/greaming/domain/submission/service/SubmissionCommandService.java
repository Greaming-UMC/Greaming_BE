package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.challenge.enums.JourneyLevel; // [추가]
import com.umc.greaming.domain.challenge.repository.WeeklyUserScoreRepository; // [추가]
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.common.s3.service.S3Service;
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
import com.umc.greaming.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionCommandService {

    private final SubmissionRepository submissionRepository;
    private final CommentRepository commentRepository;
    private final SubmissionImageRepository submissionImageRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final WeeklyUserScoreRepository weeklyUserScoreRepository; // [추가] 레벨 조회를 위해 필요
    private final S3Service s3Service;

    // [수정] User 파라미터 추가
    public SubmissionInfo updateSubmission(Long submissionId, SubmissionUpdateRequest request, User user) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        // [수정] 권한 검증 로직 활성화 (작성자 본인인지 확인)
        if (!submission.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.FORBIDDEN); // 에러 상태 코드는 프로젝트 정의에 맞게 확인
        }

        if (request.title() != null) {
            submission.setTitle(request.title());
        }
        if (request.caption() != null) {
            submission.setCaption(request.caption());
        }

        // 이미지와 태그 업데이트 로직은 기존 코드 유지 (실제 DB 반영 로직은 생략된 상태로 보임)
        List<String> currentImageUrls = request.imageList().stream()
                .map(s3Service::getPublicUrl)
                .toList();

        List<String> currentTags = request.tags();

        String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());

        // [추가] 레벨 조회 로직
        String level = getLevelBySubmission(submission);

        // [수정] DTO 변경에 맞춰 level, isLiked 전달 (본인이 수정 중이므로 좋아요 여부는 false 혹은 별도 조회 필요)
        // 여기서는 false로 두거나, 필요 시 Repository 조회
        return SubmissionInfo.from(submission, profileImageUrl, level, currentImageUrls, currentTags, false);
    }

    // [수정] User 파라미터 추가
    public void deleteSubmission(Long submissionId, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        // [수정] 권한 검증 로직 활성화
        if (!submission.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }

        submissionRepository.softDeleteById(submissionId);
    }

    // [수정] User 파라미터 추가
    public SubmissionInfo createSubmission(SubmissionCreateRequest request, User user) {

        // [삭제] 하드코딩 제거 (Long userId = 1L;)

        // [수정] 전달받은 user 객체 사용
        Submission submission = Submission.builder()
                .user(user) // 파라미터로 받은 user 사용
                .title(request.title())
                .caption(request.caption())
                .visibility(request.visibility())
                .commentEnabled(request.commentEnabled())
                .field(request.field())
                .thumbnailKey(request.thumbnailKey())
                // .challenge(...) // 챌린지 정보가 Request에 있다면 여기서 설정 필요
                .build();

        submissionRepository.save(submission);

        if (request.imageList() != null && !request.imageList().isEmpty()) {
            List<SubmissionImage> images = IntStream.range(0, request.imageList().size())
                    .mapToObj(i -> SubmissionImage.builder()
                            .submission(submission)
                            .imageKey(request.imageList().get(i))
                            .sortOrder(i)
                            .build())
                    .toList();
            submissionImageRepository.saveAll(images);
        }
        if (request.tags() != null && !request.tags().isEmpty()) {
            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                SubmissionTag submissionTag = SubmissionTag.builder()
                        .submission(submission)
                        .tag(tag)
                        .build();
                submissionTagRepository.save(submissionTag);
            }
        }

        List<String> imageUrls = request.imageList() != null
                ? request.imageList().stream().map(s3Service::getPublicUrl).toList()
                : List.of();

        String profileImageUrl = s3Service.getPublicUrl(user.getProfileImageKey());

        // [추가] 레벨 조회 (신규 생성 시 챌린지가 있다면 해당 챌린지 점수 조회)
        String level = getLevelBySubmission(submission);

        // [수정] DTO 규격에 맞춰 반환
        return SubmissionInfo.from(submission, profileImageUrl, level, imageUrls, request.tags(), false);
    }

    // [Helper] 레벨 조회 중복 코드 분리
    private String getLevelBySubmission(Submission submission) {
        // Submission에 Challenge가 연결되어 있지 않을 경우 대비 (null safe)
        if (submission.getChallenge() == null) {
            return JourneyLevel.SKETCHER.name();
        }
        return weeklyUserScoreRepository.findByUserAndChallenge(submission.getUser(), submission.getChallenge())
                .map(score -> score.getJourneyLevel().name())
                .orElse(JourneyLevel.SKETCHER.name());
    }
}