package com.umc.greaming.domain.comment.dto.response;

import com.umc.greaming.domain.comment.dto.CommentInfo;
import com.umc.greaming.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public record CommentPageResponse(
        List<CommentInfo> comments,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean isLast
) {
    public static CommentPageResponse from(Page<Comment> commentPage, List<CommentInfo> commentInfos) {
        return new CommentPageResponse(
                commentInfos,
                commentPage.getNumber() + 1,
                commentPage.getTotalPages(),
                commentPage.getTotalElements(),
                commentPage.isLast()
        );
    }
}