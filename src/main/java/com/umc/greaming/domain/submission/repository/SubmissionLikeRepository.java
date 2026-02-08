package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.like.entity.SubmissionLike;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionLikeRepository extends JpaRepository<SubmissionLike, Long> {

    boolean existsByUserAndSubmission(User user, Submission submission);
}