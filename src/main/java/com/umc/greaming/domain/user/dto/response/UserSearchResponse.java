package com.umc.greaming.domain.user.dto.response;

import com.umc.greaming.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "유저 닉네임 검색 응답")
public record UserSearchResponse(
        @Schema(description = "검색 결과 목록")
        List<UserSearchItem> users
) {
    public static UserSearchResponse from(List<User> users, java.util.function.Function<String, String> urlResolver) {
        List<UserSearchItem> items = users.stream()
                .map(user -> UserSearchItem.from(user, urlResolver))
                .toList();
        return new UserSearchResponse(items);
    }

    @Schema(description = "검색된 유저 정보")
    public record UserSearchItem(
            @Schema(description = "유저 ID", example = "1")
            Long userId,

            @Schema(description = "닉네임", example = "그림쟁이")
            String nickname,

            @Schema(description = "프로필 이미지 URL", example = "https://s3.amazonaws.com/...")
            String profileImgUrl
    ) {
        public static UserSearchItem from(User user, java.util.function.Function<String, String> urlResolver) {
            return new UserSearchItem(
                    user.getUserId(),
                    user.getNickname(),
                    urlResolver.apply(user.getProfileImageKey())
            );
        }
    }
}
