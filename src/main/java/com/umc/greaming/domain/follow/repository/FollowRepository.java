package com.umc.greaming.domain.follow.repository;

import com.umc.greaming.domain.follow.entity.Follow;
import com.umc.greaming.domain.follow.enums.FollowState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countByFollowing_IdAndState(Long userId, FollowState state);

    long countByFollower_IdAndState(Long userId, FollowState state);
}
