package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
}
