package pretzel.dreamketcherbe.domain.auth.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class AuthException extends BaseException {

  public AuthException(ExceptionType exceptionType) {
    super(exceptionType);
  }
}
