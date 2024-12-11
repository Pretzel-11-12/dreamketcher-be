package pretzel.dreamketcherbe.domain.admin.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum ManageWebtoonExceptionType implements ExceptionType {
    MANAGE_WEBTOON_NOT_FOUND(HttpStatus.NOT_FOUND, "웸툰 관리를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ManageWebtoonExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String message() {
        return null;
    }

    @Override
    public String code() {
        return null;
    }

    @Override
    public HttpStatus status() {
        return null;
    }
}
