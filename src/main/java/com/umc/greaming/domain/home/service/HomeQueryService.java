package com.umc.greaming.domain.home.service;

import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.domain.challenge.dto.ChallengeInfo;
import com.umc.greaming.domain.challenge.enums.Cycle;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import com.umc.greaming.domain.challenge.repository.ChallengeRepository;
import com.umc.greaming.domain.circle.entity.Circle;
import com.umc.greaming.domain.circle.repository.CircleMemberRepository;
import com.umc.greaming.domain.home.dto.response.HomeResponse;
import com.umc.greaming.domain.user.dto.UserHomeInfo;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeQueryService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final S3Service s3Service;

    public HomeResponse getHomeData(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        ChallengeInfo dailyInfo = challengeRepository.findCurrentChallenge(Cycle.DAILY, now)
                .map(ChallengeInfo::from).orElse(null);

        ChallengeInfo weeklyInfo = challengeRepository.findCurrentChallenge(Cycle.WEEKLY, now)
                .map(ChallengeInfo::from).orElse(null);

        UserHomeInfo userInfo = null;

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                String userProfileUrl = s3Service.getPublicUrl(user.getProfileImageKey());
                String level = JourneyLevel.SKETCHER.name();
                Circle circle = circleMemberRepository.findCircleByUserId(userId).orElse(null);

                String circleProfileUrl = null;
                if (circle != null && circle.getProfileImageKey() != null) {
                    circleProfileUrl = s3Service.getPublicUrl(circle.getProfileImageKey());
                }

                userInfo = UserHomeInfo.of(user, userProfileUrl, level, circle, circleProfileUrl);
            }
        }

        return HomeResponse.from(dailyInfo, weeklyInfo, userInfo);
    }
}