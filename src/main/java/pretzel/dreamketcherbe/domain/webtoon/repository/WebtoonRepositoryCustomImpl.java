package pretzel.dreamketcherbe.domain.webtoon.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static pretzel.dreamketcherbe.domain.webtoon.entity.QLike.like;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QWebtoon.webtoon;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QWebtoonGenre.webtoonGenre;

@RequiredArgsConstructor
public class WebtoonRepositoryCustomImpl implements WebtoonRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 웹툰 장르별 목록 조회
     */
    @Override
    public PageResDto<WebtoonResDto> findWebtoonsWithPage(String status, PageReqDto pageReqDto) {
        List<WebtoonResDto> content = getWebtoons(status, pageReqDto);
        long total = getTotalDataCount(status, pageReqDto);

        return new PageResDto<>(content, total);
    }

    /**
     * 페이징 결과 조회
     */
    private List<WebtoonResDto> getWebtoons(String status, PageReqDto pageReqDto) {
        return jpaQueryFactory.select(
                    Projections.constructor(WebtoonResDto.class, webtoon.id, webtoon.thumbnail, webtoon.member.name, webtoon.title))
            .from(webtoon)
            .join(webtoonGenre).on(webtoonGenre.webtoon.id.eq(webtoon.id))
            .where(getWhereConditions(status, pageReqDto))
            .offset(pageReqDto.getFirstIndex())
            .limit(pageReqDto.getSize())
            .orderBy(getOrderConditions(pageReqDto))
            .fetch();
    }

    /**
     * 전체 데이터 수 조회
     */
    private long getTotalDataCount(String status, PageReqDto pageReqDto) {
        return Optional.ofNullable(jpaQueryFactory
                .select(webtoon.count())
                .from(webtoon)
                .join(webtoonGenre).on(webtoonGenre.webtoon.id.eq(webtoon.id))
                .where(getWhereConditions(status, pageReqDto))
                .fetchOne())
            .orElse(0L);
    }

    /**
     * 조회 조건
     */
    private BooleanBuilder getWhereConditions(String status, PageReqDto pageReqDto) {
        final String genre = pageReqDto.getGenre();
        BooleanBuilder builder = new BooleanBuilder();

        return builder
            .and(genre.equals("none") ? null : webtoonGenre.genre.name.eq(genre))
            .and(status.equals("NEW")
                ? webtoon.status.eq(WebtoonStatus.IN_SERIES.getStatus())
                    .and(webtoon.createdAt.after(LocalDateTime.now().minusMonths(1)))
                : webtoon.status.eq(status));
    }

    /**
     * 정렬 조건
     */
    private OrderSpecifier<?> getOrderConditions(PageReqDto pageReqDto) {
        final String order = pageReqDto.getOrder();

        return switch (order) {
            case "latest" -> webtoon.createdAt.desc();
            case "stars" -> webtoon.averageStar.desc();
            case "likes" -> new OrderSpecifier<>(
                Order.DESC,
                JPAExpressions
                    .select(like.count())
                    .from(like)
                    .where(like.webtoon.id.eq(webtoon.id))
            );
            default -> throw new WebtoonException(WebtoonExceptionType.ORDER_NOT_FOUND);
        };
    }
}
