package pretzel.dreamketcherbe.domain.webtoon.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum WebtoonExceptionType implements ExceptionType {

    GENRE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 장르입니다."),
    WEBTOON_GENRE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 웹툰 장르입니다.");

    private final HttpStatus status;
    private final String message;

    WebtoonExceptionType(HttpStatus status, String message) {
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
