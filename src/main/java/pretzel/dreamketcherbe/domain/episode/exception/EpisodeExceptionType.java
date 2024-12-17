package pretzel.dreamketcherbe.domain.episode.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum EpisodeExceptionType implements ExceptionType {
  EPISODE_NOT_FOUND(HttpStatus.NOT_FOUND, "에피소드를 찾을 수 없습니다."),
  EPIOSDE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 에피소드입니다.");

  private final HttpStatus status;
  private final String message;

  EpisodeExceptionType(HttpStatus status, String message) {
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
