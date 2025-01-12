package pretzel.dreamketcherbe.domain.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.member.dto.WorkResDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static pretzel.dreamketcherbe.domain.episode.entity.QEpisode.episode;
import static pretzel.dreamketcherbe.domain.member.entity.QInterestedWebtoon.interestedWebtoon;
import static pretzel.dreamketcherbe.domain.member.entity.QMember.member;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QSerializationPeriod.serializationPeriod;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QWebtoon.webtoon;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResDto<WorkResDto> findAllWorkWithPage(Long memberId, String status, PageReqDto pageReqDto) {
        List<WorkResDto> content = getEpisodes(memberId, status, pageReqDto);
        long total = getTotalDataCount(memberId, status);

        return new PageResDto<>(content, total);
    }

    /**
     * 페이징 결과 조회
     */
    private List<WorkResDto> getEpisodes(Long memberId, String status, PageReqDto pageReqDto) {
        return jpaQueryFactory.select(
                Projections.constructor(WorkResDto.class,
                    episode.id,
                    episode.no,
                    webtoon.title,
                    webtoon.thumbnail,
                    Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", episode.publishedAt),
                    Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", serializationPeriod.startDate),
                    episode.viewCount,
                    Expressions.constant(0L),
                    JPAExpressions.select(interestedWebtoon.count())
                        .from(interestedWebtoon)
                        .where(interestedWebtoon.webtoon.id.eq(webtoon.id))))
            .from(episode)
            .join(episode.webtoon, webtoon)
            .join(episode.member, member)
            .leftJoin(serializationPeriod).on(serializationPeriod.webtoon.id.eq(webtoon.id))
            .where(getWhereConditions(memberId, status))
            .offset(pageReqDto.getFirstIndex())
            .limit(pageReqDto.getSize())
            .orderBy(episode.createdAt.desc())
            .fetch();
    }

    /**
     * 전체 데이터 수 조회
     */
    private long getTotalDataCount(Long memberId, String status) {
        return Optional.ofNullable(jpaQueryFactory
                .select(webtoon.count())
                .from(episode)
                .join(episode.webtoon, webtoon)
                .join(episode.member, member)
                .leftJoin(serializationPeriod).on(serializationPeriod.webtoon.id.eq(webtoon.id))
                .where(getWhereConditions(memberId, status))
                .fetchOne())
            .orElse(0L);
    }

    /**
     * 조회 조건
     */
    private BooleanBuilder getWhereConditions(Long memberId, String status) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(episode.member.id.eq(memberId));

        if ("all".equals(status)) {
            return builder;
        } else if ("NEW".equals(status)) {
            builder.and(webtoon.createdAt.after(LocalDateTime.now().minusMonths(1)));
        } else {
            builder.and(webtoon.status.eq(status));
        }

        return builder;
    }
}
