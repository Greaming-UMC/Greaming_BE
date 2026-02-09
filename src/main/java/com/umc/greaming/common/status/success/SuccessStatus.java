package com.umc.greaming.common.status.success;

import com.umc.greaming.common.base.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseStatus {

    //common success
    SUCCESS_200("COMM_200", HttpStatus.OK, "성공입니다."),
    SUCCESS_201("COMM_201", HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204("COMM_204", HttpStatus.NO_CONTENT, "성공입니다."),

    //auth
    AUTH_URL_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 URL 조회 성공"),
    LOGIN_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 성공"),
    LOGOUT_SUCCESS("AUTH_200", HttpStatus.OK, "로그아웃 성공"),
    CREATE_USER_SUCCESS("AUTH_201", HttpStatus.CREATED, "회원가입 성공"),
    DELETE_USER_SUCCESS("AUTH_200", HttpStatus.OK, "회원탈퇴 성공"),
    CHECK_ID_SUCCESS("AUTH_200", HttpStatus.OK, "아이디 중복 확인 성공"),
    CHECK_NICKNAME_SUCCESS("AUTH_200", HttpStatus.OK, "닉네임 중복 확인 성공"),
    UPDATE_PASSWORD_SUCCESS("AUTH_200", HttpStatus.OK, "비밀번호 변경 성공"),
    FIND_EMAIL_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 찾기 성공"),
    CHECK_EMAIL_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 중복 확인 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 코드 발송 성공"),
    CONFIRM_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 성공"),
    CREATE_TOKEN_SUCCESS("AUTH_200", HttpStatus.OK, "토큰 재발급 성공"),
    CONFIRM_PASSWORD_SUCCESS("AUTH_200", HttpStatus.OK, "비밀번호 검증 성공"),

    //작품 미리보기
    SUBMISSION_PREVIEW_SUCCESS("SUBMISSION_200", HttpStatus.OK, "작품 미리보기 조회 성공"),
    SUBMISSION_DETAIL_SUCCESS("SUBMISSION_200", HttpStatus.OK, "게시물 상세조회 성공"),
    SUBMISSION_UPDATE("SUBMISSION_200", HttpStatus.OK , "게시물 수정 성공" ),
    SUBMISSION_DELETED("SUBMISSION_200", HttpStatus.OK , "게시물 삭제 성공" ),
    SUBMISSION_CREATED("SUBMISSION_200", HttpStatus.OK , "게시물 추가 성공" ),

    //댓글
    COMMENT_LIST_SUCCESS("COMMENT_200", HttpStatus.OK , "다음 댓글 불러오기 성공" ),
    COMMENT_CREATED("COMMENT_200", HttpStatus.OK , "댓글 생성 성공" ),
    // user
    USER_PROFILE_TOP_SUCCESS("USER_200", HttpStatus.OK, "내 프로필 화면 조회 성공"),
    USER_REGIST_INFO_SUCCESS("USER_200", HttpStatus.OK, "최초 유저 정보 등록 성공"),

    //s3
    S3_UPLOAD_SUCCESS("S3_200", HttpStatus.OK, "Presigned URL 발급 성공");



    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
