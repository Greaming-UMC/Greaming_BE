package com.umc.greaming.domain.user.dto.response;

import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileSettingsResponse {

    private String nickname;
    private String profileImgUrl;
    private JourneyLevel journeyLevel;
    private String introduction;
    private List<String> specialtyTags;
    private List<String> interestTags;
    private Integer weeklyGoalScore;
}
