package com.umc.greaming.domain.comment.dto.response;

import com.umc.greaming.common.response.PageInfo;
import com.umc.greaming.domain.comment.dto.CommentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "댓글 페이지 응답")
public record CommentPageResponse(
        @Schema(description = "댓글 목록")
        List<CommentInfo> comments,

        @Schema(description = "페이지네이션 정보")
        PageInfo pageInfo
) {
    public static CommentPageResponse from(Page<?> page, List<CommentInfo> comments) {
        return new CommentPageResponse(
                comments,
                PageInfo.from(page)
        );
    }
}