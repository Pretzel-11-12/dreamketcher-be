package pretzel.dreamketcherbe.common.exception;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleBaseException(BaseException e) {
        log.warn(e.getMessage(), e);

        ExceptionType type = e.getType();
        return ResponseEntity.status(type.status())
            .body(new ExceptionResponse(type.code(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneralException(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.internalServerError()
            .body(new ExceptionResponse("INTERNAL_SERVER_ERROR", e.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        log.warn(e.getMessage(), e);

        String message = Objects.requireNonNull(e.getBindingResult().getFieldError())
            .getDefaultMessage();
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse("ARGUMENT_NOT_VALID", message));
    }
}
