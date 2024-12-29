package pretzel.dreamketcherbe.domain.comment.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum CommentExceptionType implements ExceptionType {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    RECOMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "대댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    CommentExceptionType(HttpStatus status, String message) {
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
