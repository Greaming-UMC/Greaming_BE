package com.umc.greaming.domain.submission.dto.request;

import com.umc.greaming.domain.submission.enums.SubmissionField;
import com.umc.greaming.domain.submission.enums.SubmissionVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SubmissionCreateRequest(
        @Schema(description = "게시글 제목", example = "나의 첫 번째 작품")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "게시글 내용", example = "열심히 그렸습니다.")
        String caption,

        @Schema(description = "공개 범위 (PUBLIC / PRIVATE)", example = "PUBLIC")
        @NotNull(message = "공개 범위는 필수입니다.")
        SubmissionVisibility visibility,

        @Schema(description = "작품 분야 (IL, WEBTOON 등)", example = "IL")
        @NotNull(message = "분야(field)는 필수입니다.")
        SubmissionField field, // 👈 [1] 아까 누락된 필드 추가

        @Schema(description = "썸네일 이미지 URL (압축된 버전)", example = "https://s3.../thumb_1.jpg")
        @NotBlank(message = "썸네일 이미지는 필수입니다.")
        String thumbnailUrl,   // 👈 [2] 썸네일 URL을 따로 받음

        @Schema(description = "댓글 허용 여부", example = "true")
        boolean commentEnabled,

        List<String> tags,
        List<String> imageList
) {
}