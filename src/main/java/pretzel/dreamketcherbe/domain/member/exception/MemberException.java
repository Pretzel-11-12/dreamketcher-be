package pretzel.dreamketcherbe.domain.member.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class MemberException extends BaseException {

  public MemberException(ExceptionType exceptionType) {
    super(exceptionType);
  }
}
