package com.umc.greaming.domain.user.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.domain.follow.enums.FollowState;
import com.umc.greaming.domain.follow.repository.FollowRepository;
import com.umc.greaming.domain.user.repository.*;
import com.umc.greaming.domain.user.dto.response.MyProfileTopResponse;
import com.umc.greaming.domain.user.dto.response.MyProfileSettingsResponse;
import com.umc.greaming.domain.user.dto.response.UserInfoResponse;
import com.umc.greaming.domain.user.dto.response.UserSearchResponse;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserJourny;
import com.umc.greaming.domain.user.enums.UserState;
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
    private final UserJournyRepository userJournyRepository;
    private final S3Service s3Service;

    public MyProfileTopResponse getMyProfileTop(Long userId) {

        if (userId == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        long followerCount = followRepository.countByFollowing_UserIdAndState(userId, FollowState.COMPLETED);
        long followingCount = followRepository.countByFollower_UserIdAndState(userId, FollowState.COMPLETED);

        List<String> specialtyTags = userSpecialtyTagRepository.findTagNamesByUserId(userId);
        List<String> interestTags = userInterestTagRepository.findTagNamesByUserId(userId);

        String level = userJournyRepository.findByUser(user)
                .map(userJourny -> userJourny.getJourneyLevel().name())
                .orElse("SKETCHER");

        List<String> dailyChallenge = List.of();
        List<String> weeklyChallenge = List.of();

        String profileImgUrl = resolvePublicUrl(user.getProfileImageKey());

        MyProfileTopResponse response = MyProfileTopResponse.builder()
                .userInformation(MyProfileTopResponse.UserInformation.builder()
                        .userId(userId)
                        .nickname(user.getNickname())
                        .profileImgUrl(profileImgUrl)
                        .level(level)
                        .introduction(user.getIntroduction())
                        .followerCount(followerCount)
                        .followingCount(followingCount)
                        .specialtyTags(specialtyTags != null ? specialtyTags : List.of())
                        .interestTags(interestTags != null ? interestTags : List.of())
                        .build())
                .challengeCalendar(MyProfileTopResponse.ChallengeCalendar.builder()
                        .dailyChallenge(dailyChallenge)
                        .weeklyChallenge(weeklyChallenge)
                        .build())
                .build();

        return response;
    }

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserJourny journey = userJournyRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        List<String> specialtyTags = userSpecialtyTagRepository.findTagNamesByUserId(userId);
        List<String> interestTags = userInterestTagRepository.findTagNamesByUserId(userId);

        return new UserInfoResponse(
                user.getNickname(),
                user.getIntroduction(),
                resolvePublicUrl(user.getProfileImageKey()),
                specialtyTags,
                interestTags,
                journey.getJourneyLevel(),
                journey.getWeeklyGoalScore()
        );
    }

    public UserSearchResponse searchByNickname(String nickname) {
        List<User> users = userRepository.findByNicknameContainingAndUserState(nickname, UserState.ACTIVE);
        return UserSearchResponse.from(users, this::resolvePublicUrl);
    }

    public boolean checkNicknameAvailability(String nickname) {
        return !userRepository.findByNickname(nickname).isPresent();
    }

    public MyProfileSettingsResponse getMyProfileSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserJourny journey = userJournyRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        List<String> specialtyTags = userSpecialtyTagRepository.findTagNamesByUserId(userId);
        List<String> interestTags = userInterestTagRepository.findTagNamesByUserId(userId);

        String profileImgUrl = resolvePublicUrl(user.getProfileImageKey());

        return MyProfileSettingsResponse.builder()
                .nickname(user.getNickname())
                .profileImgUrl(profileImgUrl)
                .journeyLevel(journey.getJourneyLevel())
                .introduction(user.getIntroduction())
                .specialtyTags(specialtyTags)
                .interestTags(interestTags)
                .weeklyGoalScore(journey.getWeeklyGoalScore())
                .build();
    }

    private String resolvePublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return s3Service.getPublicUrl(key);
    }
}
