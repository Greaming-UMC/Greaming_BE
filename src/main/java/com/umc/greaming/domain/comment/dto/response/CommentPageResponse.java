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
    // [수정] Page객체(메타데이터용) + 변환된 Info 리스트(데이터용)를 같이 받음
    public static CommentPageResponse from(Page<Comment> commentPage, List<CommentInfo> commentInfos) {
        return new CommentPageResponse(
                commentInfos, // 변환된 Info 리스트 사용
                commentPage.getNumber() + 1,
                commentPage.getTotalPages(),
                commentPage.getTotalElements(),
                commentPage.isLast()
        );
    }
}