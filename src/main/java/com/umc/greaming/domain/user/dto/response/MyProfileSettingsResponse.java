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
public class MyProfileSettingsResponse {

    private String nickname;
    private String profileImgUrl;
    private String level;
    private String introduction;
    private List<String> specialtyTags;
    private List<String> interestTags;
    private Integer weeklyGoalScore;
}
