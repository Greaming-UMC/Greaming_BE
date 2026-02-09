package com.umc.greaming.domain.submission.dto.request;

import com.umc.greaming.domain.submission.enums.SubmissionVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SubmissionUpdateRequest(
        @Schema(description = "수정할 게시글 제목 (null일 경우 변경 없음)", example = "수정된 제목")
        String title,

        @Schema(description = "수정할 게시글 내용 (null일 경우 변경 없음)", example = "내용을 수정합니다.")
        String caption,

        @Schema(description = "수정할 공개 범위 (null일 경우 변경 없음)", example = "PRIVATE")
        SubmissionVisibility visibility,

        @Schema(description = "수정할 댓글 허용 여부 (null일 경우 변경 없음)", example = "false")
        Boolean commentEnabled,

        @Schema(description = "수정할 태그 목록 (보낼 경우 기존 태그 싹 지우고 이걸로 교체됨)", example = "[\"수정태그1\", \"태그2\"]")
        List<String> tags,

        @Schema(description = "수정할 본문 이미지 S3 Key 목록 (보낼 경우 기존 이미지 싹 지우고 이걸로 교체됨)", example = "[\"submissions/user1/new_img1.jpg\"]")
        List<String> imageList
) {}