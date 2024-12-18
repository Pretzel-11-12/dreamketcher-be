package pretzel.dreamketcherbe.domain.admin.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class AdminException extends BaseException {

    public AdminException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
