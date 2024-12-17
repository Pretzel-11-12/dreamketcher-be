package pretzel.dreamketcherbe.common.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionType {

  String message();

  String code();

  HttpStatus status();
}
