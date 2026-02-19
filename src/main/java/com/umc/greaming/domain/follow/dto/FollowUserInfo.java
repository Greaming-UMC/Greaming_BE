package com.umc.greaming.domain.follow.dto;

import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "팔로워/팔로잉 목록에서 보여지는 유저 카드 정보")
public class FollowUserInfo {

    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "닉네임", example = "그림쟁이")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://s3.../profile.jpg")
    private String profileImgUrl;

    @Schema(description = "여정 레벨", example = "PAINTER")
    private JourneyLevel journeyLevel;

    @Schema(description = "전문 분야 태그 목록", example = "[\"일러스트\", \"캐릭터\"]")
    private List<String> specialtyTags;

    @Schema(description = "관심 분야 태그 목록", example = "[\"풍경\", \"인물\"]")
    private List<String> interestTags;

    @Schema(description = "해당 유저가 나를 팔로우하는지 여부", example = "true")
    private boolean isFollower;

    @Schema(description = "내가 해당 유저를 팔로우하는지 여부", example = "false")
    private boolean isFollowing;

}
