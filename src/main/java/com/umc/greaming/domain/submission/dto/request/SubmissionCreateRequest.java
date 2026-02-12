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

        @Schema(description = "공개 범위 (PUBLIC / CIRCLE)", example = "PUBLIC")
        @NotNull(message = "공개 범위는 필수입니다.")
        SubmissionVisibility visibility,

        @Schema(description = "작품 분야 (FREE, WEEKLY, DAILY)", example = "FREE")
        @NotNull(message = "분야(field)는 필수입니다.")
        SubmissionField field,

        @Schema(description = "썸네일 이미지 S3 Key (URL 아님)", example = "submissions/user1/thumb_uuid.jpg")
        @NotBlank(message = "썸네일 이미지는 필수입니다.")
        String thumbnailKey,

        @Schema(description = "댓글 허용 여부", example = "true")
        boolean commentEnabled,

        @Schema(description = "태그 목록 (최대 6개 권장)", example = "[\"일러스트\", \"배경\", \"캐릭터\"]")
        List<String> tags,

        @Schema(description = "본문 이미지 S3 Key 목록", example = "[\"submissions/user1/img1.jpg\", \"submissions/user1/img2.jpg\"]")
        List<String> imageList
) {
}