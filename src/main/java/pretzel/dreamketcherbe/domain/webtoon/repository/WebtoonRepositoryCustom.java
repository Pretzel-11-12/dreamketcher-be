package pretzel.dreamketcherbe.domain.webtoon.repository;

import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;

public interface WebtoonRepositoryCustom {

    PageResDto<WebtoonResDto> findWebtoonsWithPage(String status, PageReqDto pageReqDto);
}
