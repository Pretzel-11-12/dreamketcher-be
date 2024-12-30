package pretzel.dreamketcherbe.common.exception.redis;

import pretzel.dreamketcherbe.common.exception.BaseException;

public class RedisException extends BaseException {

    public RedisException(RedisExceptionType excpetionType) {
        super(excpetionType);
    }
}
