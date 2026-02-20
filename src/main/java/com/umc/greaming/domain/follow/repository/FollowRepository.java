package com.umc.greaming.domain.follow.repository;

import com.umc.greaming.domain.follow.entity.Follow;
import com.umc.greaming.domain.follow.enums.FollowState;
import com.umc.greaming.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countByFollowing_UserIdAndState(Long userId, FollowState state);

    long countByFollower_UserIdAndState(Long userId, FollowState state);

    boolean existsByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);

    void deleteByFollowerAndFollowing(User follower, User following);

    // 내가 팔로잉하는 사람 목록 (팔로잉 탭)
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower.userId = :userId AND f.state = 'COMPLETED'")
    Page<Follow> findFollowingsByUserId(@Param("userId") Long userId, Pageable pageable);

    // 나를 팔로우하는 사람 목록 (팔로워 탭)
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following.userId = :userId AND f.state = 'COMPLETED'")
    Page<Follow> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

}
