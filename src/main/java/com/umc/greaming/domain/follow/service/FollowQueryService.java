package com.umc.greaming.domain.follow.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.response.PageInfo;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.domain.follow.dto.FollowListResponse;
import com.umc.greaming.domain.follow.dto.FollowUserInfo;
import com.umc.greaming.domain.follow.entity.Follow;
import com.umc.greaming.domain.follow.repository.FollowRepository;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserJournyRepository;
import com.umc.greaming.domain.user.repository.UserRepository;
import com.umc.greaming.domain.user.repository.UserSpecialtyTagRepository;
import com.umc.greaming.domain.user.repository.UserInterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowQueryService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserJournyRepository userJournyRepository;
    private final UserSpecialtyTagRepository userSpecialtyTagRepository;
    private final UserInterestTagRepository userInterestTagRepository;
    private final S3Service s3Service;

    // 내가 팔로잉하는 사람 목록
    public FollowListResponse getFollowings(Long targetUserId, Long myUserId, int page, int size) {
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Follow> followPage = followRepository.findFollowingsByUserId(targetUserId, pageable);

        List<FollowUserInfo> users = followPage.getContent().stream()
                .map(follow -> buildFollowUserInfo(follow.getFollowing(), myUserId))
                .toList();

        return FollowListResponse.of(users, PageInfo.from(followPage));
    }

    // 나를 팔로우하는 사람 목록
    public FollowListResponse getFollowers(Long targetUserId, Long myUserId, int page, int size) {
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Follow> followPage = followRepository.findFollowersByUserId(targetUserId, pageable);

        List<FollowUserInfo> users = followPage.getContent().stream()
                .map(follow -> buildFollowUserInfo(follow.getFollower(), myUserId))
                .toList();

        return FollowListResponse.of(users, PageInfo.from(followPage));
    }

    private FollowUserInfo buildFollowUserInfo(User target, Long myUserId) {
        JourneyLevel journeyLevel = userJournyRepository.findByUser(target)
                .map(j -> j.getJourneyLevel())
                .orElse(JourneyLevel.SKETCHER);

        List<String> specialtyTags = userSpecialtyTagRepository.findTagNamesByUserId(target.getUserId());
        List<String> interestTags = userInterestTagRepository.findTagNamesByUserId(target.getUserId());

        String profileImgUrl = target.getProfileImageKey() != null
                ? s3Service.getPublicUrl(target.getProfileImageKey())
                : null;

        // myUserId 기준으로 관계 계산 (비로그인 시 false)
        boolean isFollower = myUserId != null &&
                followRepository.existsByFollower_UserIdAndFollowing_UserId(target.getUserId(), myUserId);
        boolean isFollowing = myUserId != null &&
                followRepository.existsByFollower_UserIdAndFollowing_UserId(myUserId, target.getUserId());

        return FollowUserInfo.builder()
                .userId(target.getUserId())
                .nickname(target.getNickname())
                .profileImgUrl(profileImgUrl)
                .journeyLevel(journeyLevel)
                .specialtyTags(specialtyTags)
                .interestTags(interestTags)
                .isFollower(isFollower)
                .isFollowing(isFollowing)
                .build();
    }
}
