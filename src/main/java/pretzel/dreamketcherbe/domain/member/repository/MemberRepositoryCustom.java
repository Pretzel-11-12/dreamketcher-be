package pretzel.dreamketcherbe.domain.member.repository;

import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.member.dto.WorkResDto;

public interface MemberRepositoryCustom {

    PageResDto<WorkResDto> findAllWorkWithPage(Long memberId, String status, PageReqDto pageReqDto);
}
