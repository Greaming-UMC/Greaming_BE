package com.umc.greaming.domain.follow.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.follow.dto.FollowResponse;
import com.umc.greaming.domain.follow.entity.Follow;
import com.umc.greaming.domain.follow.enums.FollowState;
import com.umc.greaming.domain.follow.repository.FollowRepository;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FollowCommandService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowResponse toggleFollow(Long targetUserId, User me) {
        // 자기 자신 팔로우 방지
        if (me.getUserId().equals(targetUserId)) {
            throw new GeneralException(ErrorStatus.CANNOT_FOLLOW_SELF);
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        boolean isFollowing;

        if (followRepository.existsByFollower_UserIdAndFollowing_UserId(me.getUserId(), targetUserId)) {
            followRepository.deleteByFollowerAndFollowing(me, target);
            isFollowing = false;
            log.info("팔로우 취소 - follower: {}, following: {}", me.getUserId(), targetUserId);
        } else {
            Follow follow = Follow.builder()
                    .follower(me)
                    .following(target)
                    .state(FollowState.COMPLETED)
                    .build();
            followRepository.save(follow);
            isFollowing = true;
            log.info("팔로우 추가 - follower: {}, following: {}", me.getUserId(), targetUserId);
        }

        long followerCount = followRepository.countByFollowing_UserIdAndState(targetUserId, FollowState.COMPLETED);
        return FollowResponse.of(isFollowing, followerCount);
    }
}
