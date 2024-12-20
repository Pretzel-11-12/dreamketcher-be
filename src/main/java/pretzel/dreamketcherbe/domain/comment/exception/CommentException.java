package pretzel.dreamketcherbe.domain.comment.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class CommentException extends BaseException {

    public CommentException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}