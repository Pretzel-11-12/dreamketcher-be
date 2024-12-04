package pretzel.dreamketcherbe.common.utils.S3Utils.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class S3ErrorResponse {
    private S3ErrorCode code;
    private String message;

}
