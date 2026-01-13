package com.umc.greaming.common.status.success;

import com.umc.greaming.common.base.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseStatus {

    // 공용 성공
    SUCCESS_200("GREAMING_200", HttpStatus.OK, "성공입니다."),
    SUCCESS_201("GREAMING_201", HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204("GREAMING_204", HttpStatus.NO_CONTENT, "성공입니다."),

    //작품 미리보기
    WORK_PREVIEW_SUCCESS("WORK_200", HttpStatus.OK, "작품 미리보기 조회 성공");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
