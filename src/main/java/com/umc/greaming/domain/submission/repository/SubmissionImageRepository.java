package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.Submission; // [추가]
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionImageRepository extends JpaRepository<SubmissionImage, Long> {

    List<SubmissionImage> findAllBySubmissionIdOrderBySortOrderAsc(Long submissionId);

    // [추가] 게시글 수정 시 기존 이미지를 싹 지우기 위해 필수!
    void deleteAllBySubmission(Submission submission);

    void deleteAllBySubmission(Submission submission);
}