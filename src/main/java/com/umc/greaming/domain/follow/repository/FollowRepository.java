package com.umc.greaming.domain.follow.repository;

import com.umc.greaming.domain.follow.entity.Follow;
import com.umc.greaming.domain.follow.enums.FollowState;
import com.umc.greaming.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로워 수 조회 (나를 팔로우 하는 사람)
    long countByFollowing_UserIdAndState(Long userId, FollowState state);

    // 팔로잉 수 조회 (내가 팔로우 하는 사람)
    long countByFollower_UserIdAndState(Long userId, FollowState state);

    // 이미 팔로우 중인지 확인
    boolean existsByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);

    // 팔로우 관계 조회
    // Optional<Follow> findByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);
}