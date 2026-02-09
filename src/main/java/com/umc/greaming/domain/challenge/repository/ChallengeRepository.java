package com.umc.greaming.domain.challenge.repository;

import com.umc.greaming.domain.challenge.entity.Challenge;
import com.umc.greaming.domain.challenge.enums.Cycle;
import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT c FROM Challenge c " +
            "WHERE c.cycle = :cycle " +
            "AND c.startAt <= :now " +
            "AND c.endAt >= :now " +
            "ORDER BY c.startAt DESC LIMIT 1")
    Optional<Challenge> findCurrentChallenge(@Param("cycle") Cycle cycle, @Param("now") LocalDateTime now);


}