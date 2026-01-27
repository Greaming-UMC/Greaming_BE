package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
