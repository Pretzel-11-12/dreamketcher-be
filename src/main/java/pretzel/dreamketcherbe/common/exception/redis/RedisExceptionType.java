package pretzel.dreamketcherbe.common.exception.redis;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum RedisExceptionType implements ExceptionType {
  SERIALIZE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 직렬화 중 오류가 발생했습니다."),
  DESERIALIZE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 역직렬화 중 오류가 발생했습니다."),
  REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 연결 중 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String message;

  RedisExceptionType(HttpStatus status, String message) {
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
