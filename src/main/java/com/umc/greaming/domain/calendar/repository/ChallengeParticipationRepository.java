package com.umc.greaming.domain.calendar.repository;

import com.umc.greaming.domain.calendar.entity.ChallengeParticipation;
import com.umc.greaming.domain.calendar.enums.ChallengeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {

    @Query("""
        select distinct cp.participatedAt
        from ChallengeParticipation cp
        where cp.userId = :userId
          and cp.challengeType = :type
          and cp.participatedAt between :start and :end
        order by cp.participatedAt asc
    """)
    List<LocalDate> findDistinctParticipatedAt(
            @Param("userId") Long userId,
            @Param("type") ChallengeType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
