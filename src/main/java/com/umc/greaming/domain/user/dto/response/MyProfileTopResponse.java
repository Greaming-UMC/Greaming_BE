package com.umc.greaming.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileTopResponse {

    private UserInformation userInformation;
    private ChallengeCalendar challengeCalendar;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInformation {
        private Long userId;
        private String nickname;
        private String profileImgUrl;
        private String level;
        private String introduction;
        private Long followerCount;
        private Long followingCount;
        private List<String> specialtyTags;
        private List<String> interestTags;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeCalendar {
        private List<String> dailyChallenge;
        private List<String> weeklyChallenge;
    }
}
