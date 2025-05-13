package pretzel.dreamketcherbe.domain.member.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum StorageFolderExceptionType implements ExceptionType {
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 폴더를 찾을 수 없습니다."),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이템을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    StorageFolderExceptionType(HttpStatus status, String message) {
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
