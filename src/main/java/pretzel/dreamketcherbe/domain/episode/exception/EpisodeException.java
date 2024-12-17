package pretzel.dreamketcherbe.domain.episode.exception;

import pretzel.dreamketcherbe.common.exception.BaseException;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public class EpisodeException extends BaseException {

  public EpisodeException(ExceptionType exceptionType) {
    super(exceptionType);
  }
}
