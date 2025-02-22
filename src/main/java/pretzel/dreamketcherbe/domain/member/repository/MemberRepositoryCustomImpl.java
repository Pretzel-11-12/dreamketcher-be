package pretzel.dreamketcherbe.domain.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.member.dto.WorkContentDto;
import pretzel.dreamketcherbe.domain.member.dto.WorkResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static pretzel.dreamketcherbe.domain.comment.entity.QComment.comment;
import static pretzel.dreamketcherbe.domain.comment.entity.QRecomment.recomment;
import static pretzel.dreamketcherbe.domain.episode.entity.QEpisodeStar.episodeStar;
import static pretzel.dreamketcherbe.domain.member.entity.QInterestedWebtoon.interestedWebtoon;
import static pretzel.dreamketcherbe.domain.member.entity.QMember.member;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QLike.like;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QSerializationPeriod.serializationPeriod;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QWebtoon.webtoon;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public WorkResDto findAllWorkWithPage(Long memberId, String status, PageReqDto pageReqDto) {
        List<WorkContentDto> content = getWorks(memberId, status, pageReqDto);
        long total = getTotalDataCount(memberId, status);

        long inSeriesCount = getWorkStatusDataCount(memberId, WebtoonStatus.IN_SERIES.getStatus());
        long finishCount = getWorkStatusDataCount(memberId, WebtoonStatus.FINISH.getStatus());
        long newCount = getWorkStatusDataCount(memberId, WebtoonStatus.NEW.getStatus());
        long restCount = getWorkStatusDataCount(memberId, WebtoonStatus.REST.getStatus());
        long preSeriesCount = getWorkStatusDataCount(memberId,
            WebtoonStatus.PRE_SERIES.getStatus());

        return WorkResDto.of(new PageResDto<>(content, total), inSeriesCount, finishCount, newCount,
            restCount, preSeriesCount);
    }

    /**
     * 페이징 결과 조회
     */
    private List<WorkContentDto> getWorks(Long memberId, String status, PageReqDto pageReqDto) {
        return jpaQueryFactory.select(
                Projections.constructor(WorkContentDto.class,
                    webtoon.id,
                    webtoon.title,
                    webtoon.thumbnail,
                    member.nickname,
                    webtoon.story,
                    webtoon.genre.name,
                    webtoon.episodeCount,
                    Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", webtoon.updatedAt),
                    Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')",
                        serializationPeriod.startDate),
                    webtoon.averageStar,
                    JPAExpressions.select(episodeStar.id.countDistinct())
                        .from(episodeStar)
                        .where(episodeStar.webtoon.id.eq(webtoon.id)),
                    JPAExpressions.select(like.count())
                        .from(like)
                        .where(like.webtoon.id.eq(webtoon.id)),
                    JPAExpressions.select(comment.count().add(
                            JPAExpressions.select(recomment.count())
                                .from(recomment)
                                .where(recomment.webtoon.id.eq(webtoon.id))
                        ))
                        .from(comment)
                        .where(comment.webtoon.id.eq(webtoon.id)),
                    JPAExpressions.select(interestedWebtoon.count())
                        .from(interestedWebtoon)
                        .where(interestedWebtoon.webtoon.id.eq(webtoon.id))
                ))
            .from(webtoon)
            .join(webtoon.member, member)
            .leftJoin(serializationPeriod).on(serializationPeriod.webtoon.id.eq(webtoon.id))
            .where(getWhereConditions(memberId, status))
            .offset(pageReqDto.getFirstIndex())
            .limit(pageReqDto.getSize())
            .orderBy(webtoon.updatedAt.desc())
            .fetch();
    }

    /**
     * 전체 데이터 수 조회
     */
    private long getTotalDataCount(Long memberId, String status) {
        return Optional.ofNullable(jpaQueryFactory
                .select(webtoon.count())
                .from(webtoon)
                .join(webtoon.member, member)
                .leftJoin(serializationPeriod).on(serializationPeriod.webtoon.id.eq(webtoon.id))
                .where(getWhereConditions(memberId, status))
                .fetchOne())
            .orElse(0L);
    }

    /**
     * 유저가 소장한 상태별 작품 데이터 개수
     */
    private long getWorkStatusDataCount(Long memberId, String status) {
        return Optional.ofNullable(jpaQueryFactory
                .select(webtoon.count())
                .from(webtoon)
                .where(getWhereConditions(memberId, status))
                .fetchOne())
            .orElse(0L);
    }

    /**
     * 조회 조건
     */
    private BooleanBuilder getWhereConditions(Long memberId, String status) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(webtoon.member.id.eq(memberId));

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
