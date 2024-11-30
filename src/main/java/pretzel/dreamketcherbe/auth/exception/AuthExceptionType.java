package pretzel.dreamketcherbe.auth.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum AuthExceptionType implements ExceptionType {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 제공되지 않았습니다.");

    private final HttpStatus status;
    private final String message;

    AuthExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
