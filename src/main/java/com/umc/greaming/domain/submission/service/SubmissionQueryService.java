package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionQueryService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;

    public SubmissionPreviewResponse getSubmissionPreview(Long workId) {
        Submission work = submissionRepository.findById(workId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WORK_NOT_FOUND));

        List<String> tags = submissionTagRepository.findTagNamesByWorkId(workId);

        return SubmissionPreviewResponse.from(work, tags);
    }
}
