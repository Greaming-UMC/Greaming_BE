package com.umc.greaming.domain.user.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.domain.follow.enums.FollowState;
import com.umc.greaming.domain.follow.repository.FollowRepository;
import com.umc.greaming.domain.tag.repository.UserInterestTagRepository;
import com.umc.greaming.domain.tag.repository.UserSpecialtyTagRepository;
import com.umc.greaming.domain.user.dto.response.MyProfileTopResponse;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserSpecialtyTagRepository userSpecialtyTagRepository;
    private final UserInterestTagRepository userInterestTagRepository;
    private final S3Service s3Service;

    public MyProfileTopResponse getMyProfileTop(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        long followerCount = followRepository.countByFollowing_IdAndState(userId, FollowState.COMPLETED);
        long followingCount = followRepository.countByFollower_IdAndState(userId, FollowState.COMPLETED);

        List<String> specialtyTags = userSpecialtyTagRepository.findTagNamesByUserId(userId);
        List<String> interestTags = userInterestTagRepository.findTagNamesByUserId(userId);

        String level = "Painter";

        List<String> dailyChallenge = List.of();
        List<String> weeklyChallenge = List.of();

        String profileImgUrl = resolvePublicUrl(user.getProfileImageKey());

        return MyProfileTopResponse.builder()
                .userInformation(MyProfileTopResponse.UserInformation.builder()
                        .nickname(user.getNickname())
                        .profileImgUrl(profileImgUrl)
                        .level(level)
                        .introduction(user.getIntroduction())
                        .followerCount(followerCount)
                        .followingCount(followingCount)
                        .specialtyTags(specialtyTags)
                        .interestTags(interestTags)
                        .build())
                .challengeCalendar(MyProfileTopResponse.ChallengeCalendar.builder()
                        .dailyChallenge(dailyChallenge)
                        .weeklyChallenge(weeklyChallenge)
                        .build())
                .build();
    }

    private String resolvePublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return s3Service.getPublicUrl(key);
    }
}
