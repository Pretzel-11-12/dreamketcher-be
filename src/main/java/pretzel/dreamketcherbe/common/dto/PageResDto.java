package pretzel.dreamketcherbe.common.dto;

import java.util.List;

public record PageResDto<T>(
        List<T> result,
        long totalElements
) {
}
