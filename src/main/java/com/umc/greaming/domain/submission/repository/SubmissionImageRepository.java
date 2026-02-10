package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionImageRepository extends JpaRepository<SubmissionImage, Long> {

    List<SubmissionImage> findAllBySubmissionIdOrderBySortOrderAsc(Long submissionId);

    void deleteAllBySubmission(Submission submission);
}