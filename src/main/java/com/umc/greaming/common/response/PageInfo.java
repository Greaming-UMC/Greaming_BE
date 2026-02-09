package com.umc.greaming.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "페이지네이션 정보")
public record PageInfo(
        @Schema(description = "현재 페이지 번호", example = "1")
        int currentPage,

        @Schema(description = "페이지 크기", example = "20")
        int pageSize,

        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,

        @Schema(description = "전체 요소 수", example = "100")
        long totalElements,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLast,

        @Schema(description = "첫 페이지 여부", example = "true")
        boolean isFirst
) {
    public static PageInfo from(Page<?> page) {
        return new PageInfo(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst()
        );
    }

    public static PageInfo of(int currentPage, int pageSize, int totalPages, long totalElements, boolean isLast, boolean isFirst) {
        return new PageInfo(
                currentPage,
                pageSize,
                totalPages,
                totalElements,
                isLast,
                isFirst
        );
    }
}
