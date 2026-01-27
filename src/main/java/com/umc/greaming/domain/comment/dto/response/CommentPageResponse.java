package com.umc.greaming.domain.comment.dto.response;

import com.umc.greaming.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public record CommentPageResponse(
        List<CommentInfo> comments,
        int currentPage,      // 현재 페이지 번호
        int totalPages,       // 전체 페이지 수
        long totalElements,   // 전체 댓글 개수
        boolean isLast        // 마지막 페이지 여부
) {
    public static CommentPageResponse from(Page<Comment> commentPage) {
        return new CommentPageResponse(
                commentPage.getContent().stream()
                        .map(CommentInfo::from)
                        .toList(),
                commentPage.getNumber()+1,
                commentPage.getTotalPages(),
                commentPage.getTotalElements(),
                commentPage.isLast()
        );
    }
}