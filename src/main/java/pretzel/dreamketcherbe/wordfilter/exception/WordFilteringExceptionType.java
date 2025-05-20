package pretzel.dreamketcherbe.wordfilter.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum WordFilteringExceptionType implements ExceptionType {

    BADWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 단어는 존재하지 않습니다."),
    BADWORD_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 단어입니다."),
    ALLOWEDWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 단어는 존재하지 않습니다."),
    ALLOWEDWORD_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 단어입니다.");

    private final HttpStatus status;
    private final String message;

    WordFilteringExceptionType(HttpStatus status, String message) {
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
