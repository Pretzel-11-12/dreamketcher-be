package pretzel.dreamketcherbe.common.utils.S3Utils.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class S3ExceptionHandler {

    @ExceptionHandler(S3Exception.class)
    public S3ErrorResponse handleS3Exception(S3Exception e, HttpServletRequest request) {
        log.error("S3 Error Code : {}, url {}, message: {}", e.getS3ErrorCode(),
            request.getRequestURI(), e.getMessage());

        return S3ErrorResponse.builder()
            .code(e.getS3ErrorCode())
            .message(e.getMessage())
            .build();
    }
}
