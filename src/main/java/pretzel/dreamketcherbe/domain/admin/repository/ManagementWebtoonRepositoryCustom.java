package pretzel.dreamketcherbe.domain.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pretzel.dreamketcherbe.domain.admin.dto.UpdateWebtoonApprovalReqDto;
import pretzel.dreamketcherbe.domain.admin.entity.Reason;
import pretzel.dreamketcherbe.domain.admin.entity.ReasonContent;

import static pretzel.dreamketcherbe.domain.admin.entity.QManagementWebtoon.managementWebtoon;
import static pretzel.dreamketcherbe.domain.admin.entity.QReason.reason;

@Repository
@RequiredArgsConstructor
public class ManagementWebtoonRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 작품 승인 변경
     */
    public void updateWebtoonApproval(UpdateWebtoonApprovalReqDto request) {
        jpaQueryFactory.update(managementWebtoon)
            .set(managementWebtoon.approval, request.approval())
            .set(managementWebtoon.reason, getReasonId(request.reason()))
            .set(managementWebtoon.detailReason, request.detailReason())
            .where(managementWebtoon.webtoon.id.in(request.webtoonIds()))
            .execute();
    }

    private Reason getReasonId(String keyword) {
        return jpaQueryFactory.selectFrom(reason)
            .where(reason.content.eq(ReasonContent.getContentByKeyword(keyword)))
            .fetchOne();
    }
}
