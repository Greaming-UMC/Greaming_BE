package com.umc.greaming.domain.user.repository;

import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserJourny;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJournyRepository extends JpaRepository<UserJourny, Long> {

    Optional<UserJourny> findByUser(User user);

    @Query("SELECT uj FROM UserJourny uj WHERE uj.user.userId = :userId")
    Optional<UserJourny> findByUserId(@Param("userId") Long userId);

    long countByJourneyLevel(JourneyLevel level);

    @Query("SELECT uj FROM UserJourny uj WHERE uj.journeyLevel = :level ORDER BY uj.goalScore DESC")
    List<UserJourny> findAllByJourneyLevelOrderByGoalScoreDesc(@Param("level") JourneyLevel level);

    @Query("SELECT uj FROM UserJourny uj ORDER BY uj.goalScore DESC, uj.createdAt ASC")
    List<UserJourny> findAllOrderByGoalScoreDesc();

    boolean existsByUser(User user);
}
