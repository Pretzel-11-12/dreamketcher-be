package pretzel.dreamketcherbe.domain.member.repository;

import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.domain.member.dto.WorkResDto;

public interface MemberRepositoryCustom {

    WorkResDto findAllWorkWithPage(Long memberId, String status, PageReqDto pageReqDto);
}
