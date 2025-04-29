package pretzel.dreamketcherbe.domain.member.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class StorageFolderException extends BaseException {
    public StorageFolderException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
