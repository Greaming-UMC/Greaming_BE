package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
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

    public SubmissionPreviewResponse getSubmissionPreview(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WORK_NOT_FOUND));

        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);

        return SubmissionPreviewResponse.from(submission, tags);
    }
    public SubmissionDetailResponse getSubmissionDetail(Long submissionId, int page) {
        SubmissionInfo submissionInfo = getSubmissionInfo(submissionId);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WORK_NOT_FOUND));

        Pageable pageable = PageRequest.of(page - 1, 30, Sort.by("createdAt").descending());

        Page<Comment> commentPage = commentRepository.findAllBySubmission(submission, pageable);

        return SubmissionDetailResponse.from(submissionInfo, commentPage);
    }

    public SubmissionInfo getSubmissionInfo(Long submissionId) {
        // 1. 엔티티 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WORK_NOT_FOUND));

        List<String> sortedImages = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submissionId)
                .stream()
                .map(SubmissionImage::getImageUrl) // URL만 추출
                .toList();

        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);

        boolean isLiked = true;

        return SubmissionInfo.from(submission, sortedImages, tags, isLiked);
    }
}
