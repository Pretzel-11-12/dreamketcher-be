package pretzel.dreamketcherbe.domain.webtoon.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class WebtoonException extends BaseException {

    public WebtoonException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
