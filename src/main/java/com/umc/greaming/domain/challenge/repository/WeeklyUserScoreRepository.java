package com.umc.greaming.domain.challenge.repository;

import com.umc.greaming.domain.challenge.entity.WeeklyUserScore;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyUserScoreRepository extends JpaRepository<WeeklyUserScore, Long> {

    Optional<WeeklyUserScore> findByUserAndChallenge(User user, Challenge challenge);

}