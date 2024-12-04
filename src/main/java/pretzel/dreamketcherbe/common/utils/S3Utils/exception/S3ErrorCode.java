package pretzel.dreamketcherbe.common.utils.S3Utils.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode {
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "이미지 파일이 아닙니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),
    INVALID_FILE_SIZE(HttpStatus.INSUFFICIENT_STORAGE, "파일 크기가 너무 큽니다."),
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
