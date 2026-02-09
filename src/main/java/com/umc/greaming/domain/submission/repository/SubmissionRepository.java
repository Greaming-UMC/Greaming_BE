package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Submission> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT s FROM Submission s " +
           "JOIN FETCH s.user " +
           "LEFT JOIN FETCH s.challenge " +
           "WHERE s.id = :id")
    Optional<Submission> findByIdWithUserAndChallenge(@Param("id") Long id);

    @Query("SELECT s FROM Submission s " +
           "JOIN FETCH s.user " +
           "WHERE s.challenge.id = :challengeId " +
           "AND s.deletedAt != null ")
    Page<Submission> findAllByChallengeId(@Param("challengeId") Long challengeId, Pageable pageable);

    @Query("SELECT s FROM Submission s " +
            "WHERE s.challenge.id = :challengeId " +
            "ORDER BY (s.likeCount * 2 + s.commentCount * 3 + s.bookmarkCount * 5) DESC, s.createdAt DESC")
    Page<Submission> findAllByChallengeIdOrderByRecommend(@Param("challengeId") Long challengeId, Pageable pageable);

}
