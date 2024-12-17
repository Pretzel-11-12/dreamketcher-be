package pretzel.dreamketcherbe.domain.auth.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum AuthExceptionType implements ExceptionType {
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 제공되지 않았습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 타입입니다."),
  DISMATCHED_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "인증 정보가 일치하지 않습니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");

  private final HttpStatus status;
  private final String message;

  AuthExceptionType(HttpStatus status, String message) {
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
