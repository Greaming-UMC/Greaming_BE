package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.SubmissionLike;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionLikeRepository extends JpaRepository<SubmissionLike, Long> {

    boolean existsByUserAndSubmission(User user, Submission submission);

    @Query("SELECT sl.submission.id FROM SubmissionLike sl WHERE sl.user.userId = :userId AND sl.submission.id IN :submissionIds")
    List<Long> findLikedSubmissionIdsByUserIdAndSubmissionIds(@Param("userId") Long userId, @Param("submissionIds") List<Long> submissionIds);
}
