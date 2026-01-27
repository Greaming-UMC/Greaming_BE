package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionCommandService {
    private final SubmissionRepository submissionRepository;
    private final CommentRepository commentRepository;
    private final SubmissionImageRepository submissionImageRepository;

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
}