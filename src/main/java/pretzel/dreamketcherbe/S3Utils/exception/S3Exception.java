package pretzel.dreamketcherbe.S3Utils.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class S3Exception extends BaseException {

    public S3Exception(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
