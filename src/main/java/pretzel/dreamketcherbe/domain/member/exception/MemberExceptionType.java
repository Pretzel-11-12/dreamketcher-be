package pretzel.dreamketcherbe.domain.member.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum MemberExceptionType implements ExceptionType {
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
  MEMBER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "해당 회원은 권한이 없습니다."),
  NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
  INTERESTED_WEBTOON_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원의 관심 웹툰을 찾을 수 없습니다."),
  ALREADY_FAVORITED(HttpStatus.BAD_REQUEST, "이미 관심 웹툰으로 등록된 웹툰입니다.");

  private final HttpStatus status;
  private final String message;

  MemberExceptionType(HttpStatus status, String message) {
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