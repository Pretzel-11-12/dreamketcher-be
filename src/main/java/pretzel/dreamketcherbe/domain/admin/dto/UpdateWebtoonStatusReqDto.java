package pretzel.dreamketcherbe.domain.admin.dto;

import java.util.List;

public record UpdateWebtoonStatusReqDto(
    List<Long> webtoonIds,
    String status
) {

}
