package pretzel.dreamketcherbe.common.utils.S3Utils.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3Exception extends RuntimeException {

    private S3ErrorCode s3ErrorCode;
    private String message;

    public S3Exception(S3ErrorCode s3ErrorCode) {
        super(s3ErrorCode.getMessage());
        this.s3ErrorCode = s3ErrorCode;
        this.message = s3ErrorCode.getMessage();
    }

}
