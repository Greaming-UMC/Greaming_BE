package com.umc.greaming.common.status.error;

import com.umc.greaming.common.base.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    // Common Errors
    BAD_REQUEST("COMM_400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_ERROR("COMM_400", HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    UNAUTHORIZED("COMM_401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN("COMM_403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND("COMM_404", HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("COMM_405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR("COMM_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    // Auth Errors
    INVALID_TOKEN("AUTH_401", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("AUTH_401", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND("AUTH_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED("AUTH_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_MISSING("AUTH_400", HttpStatus.BAD_REQUEST, "리프레시 토큰이 필요합니다."),
    OAUTH2_LOGIN_FAILED("AUTH_401", HttpStatus.UNAUTHORIZED, "소셜 로그인에 실패했습니다."),
    INVALID_SOCIAL_PROVIDER("AUTH_400", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다. (kakao, google만 지원)"),
    INVALID_PASSWORD("AUTH_400", HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    PASSWORD_SAME_AS_OLD("AUTH_400", HttpStatus.BAD_REQUEST, "새 비밀번호가 기존 비밀번호와 동일합니다."),
    USER_NOT_FOUND("AUTH_404", HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL("AUTH_409", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME("AUTH_409", HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // Submission (작품) Errors
    SUBMISSION_NOT_FOUND("SUBMISSION_404", HttpStatus.NOT_FOUND, "작품을 찾을 수 없습니다."),
    SUBMISSION_NOT_AUTHORIZED("SUBMISSION_401", HttpStatus.UNAUTHORIZED, "올바른 사용자가 아닙니다."),

    COMMENT_NOT_FOUND("COMMENT_404", HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    // [추천] 나중에 댓글 삭제/수정 기능 만들 때 필수
    COMMENT_NOT_AUTHORIZED("COMMENT_403", HttpStatus.FORBIDDEN, "댓글에 대한 수정/삭제 권한이 없습니다."),
    // [추천] 답글 전용 에러가 필요하다면 (선택)
    REPLY_NOT_FOUND("REPLY_404", HttpStatus.NOT_FOUND, "답글을 찾을 수 없습니다."),

    // Like (좋아요 - 추후 구현 대비)
    ALREADY_LIKED("LIKE_409", HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    SELF_LIKE_NOT_ALLOWED("LIKE_400", HttpStatus.BAD_REQUEST, "본인 글에는 좋아요를 누를 수 없습니다."),
    // S3 Errors
    S3_UPLOAD_FAILED("S3_500", HttpStatus.INTERNAL_SERVER_ERROR, "Presigned URL 발급에 실패했습니다.");


    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}