package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.repository.CommentRepository;
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
    private final TagRepository tagRepository; // 태그 자체를 조회/저장하기 위해 필요
    private final UserRepository userRepository;

    public SubmissionInfo updateSubmission(Long submissionId, SubmissionUpdateRequest request){ //, Long userId) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
    /*
        if (!submission.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    */
        if (request.title() != null) {
            submission.setTitle(request.title());
        }
        if (request.caption() != null) {
            submission.setCaption(request.caption());
        }

        List<String> currentImages = request.imageList();
        List<String> currentTags = request.tags();

        return SubmissionInfo.from(submission, currentImages, currentTags, false);
    }

    public void deleteSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        /*
        if (!submission.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.SUBMISSION_NOT_AUTHORIZED);
        }
        */

        submissionRepository.softDeleteById(submissionId);
    }

    public SubmissionInfo createSubmission(SubmissionCreateRequest request) {

        Long userId = 1L;
        User user = userRepository.getReferenceById(userId);

        Submission submission = Submission.builder()
                .user(user)
                .title(request.title())
                .caption(request.caption())
                .visibility(request.visibility())
                .commentEnabled(request.commentEnabled())

                .field(request.field())
                .thumbnailUrl(request.thumbnailUrl())

                .build();

        submissionRepository.save(submission);

        if (request.imageList() != null && !request.imageList().isEmpty()) {
            List<SubmissionImage> images = IntStream.range(0, request.imageList().size())
                    .mapToObj(i -> SubmissionImage.builder()
                            .submission(submission)
                            .imageUrl(request.imageList().get(i))
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

        return SubmissionInfo.from(submission, request.imageList(), request.tags(), false);
    }
}