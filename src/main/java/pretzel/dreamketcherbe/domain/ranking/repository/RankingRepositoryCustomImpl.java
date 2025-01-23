package pretzel.dreamketcherbe.domain.ranking.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import pretzel.dreamketcherbe.domain.ranking.dto.WebtoonPopularityDataDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;

import java.time.LocalDateTime;
import java.util.List;

import static pretzel.dreamketcherbe.domain.episode.entity.QEpisode.episode;
import static pretzel.dreamketcherbe.domain.episode.entity.QEpisodeStar.episodeStar;
import static pretzel.dreamketcherbe.domain.member.entity.QInterestedWebtoon.interestedWebtoon;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QLike.like;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QWebtoon.webtoon;

@RequiredArgsConstructor
public class RankingRepositoryCustomImpl implements RankingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<WebtoonPopularityDataDto> findRanking(String status, String genreType) {
        return jpaQueryFactory.select(
                Projections.constructor(WebtoonPopularityDataDto.class,
                    webtoon.id,
                    webtoon.title,
                    webtoon.member.name,
                    webtoon.description,
                    webtoon.thumbnail,
                    webtoon.genre.name,
                    webtoon.episodeCount,
                    webtoon.averageStar,
                    ExpressionUtils.as(
                        JPAExpressions.select(episodeStar.id.countDistinct())
                            .from(episodeStar)
                            .where(episodeStar.webtoon.id.eq(webtoon.id)),
                        "numOfStars"
                    ),
                    like.id.countDistinct().as("likeCount"),
                    episode.viewCount.sum().as("viewCount"),
                    interestedWebtoon.id.countDistinct().as("interestedCount"),
                    calculatePopularity(
                        webtoon.averageStar,
                        like.id.countDistinct(),
                        episode.viewCount.sum(),
                        interestedWebtoon.id.countDistinct()
                    ).as("popularity")
                )
            )
            .from(webtoon)
            .leftJoin(episode).on(episode.webtoon.id.eq(webtoon.id))
            .leftJoin(like).on(like.webtoon.id.eq(webtoon.id))
            .leftJoin(interestedWebtoon).on(interestedWebtoon.webtoon.id.eq(webtoon.id))
            .where(getWhereConditions(status, genreType))
            .groupBy(webtoon.id)
            .orderBy(calculatePopularity(
                webtoon.averageStar,
                like.id.countDistinct(),
                episode.viewCount.sum(),
                interestedWebtoon.id.countDistinct()
            ).desc())
            .limit(12)
            .fetch();
    }

    /**
     * 조회 조건
     */
    private BooleanBuilder getWhereConditions(String status, String genreType) {
        BooleanBuilder builder = new BooleanBuilder();

        return builder
            .and(genreType.equals("none") ? null : webtoon.genre.name.eq(genreType))
            .and(status.equals("NEW")
                ? webtoon.status.eq(WebtoonStatus.IN_SERIES.getStatus())
                .and(webtoon.createdAt.after(LocalDateTime.now().minusMonths(1)))
                : webtoon.status.eq(status));
    }

    /**
     * 인기도 계산
     */
    private NumberExpression<Float> calculatePopularity(NumberPath<Float> averageStar, NumberExpression<Long> likeCount, NumberExpression<Long> viewCount, NumberExpression<Long> interestedCount) {
        NumberExpression<Double> logLikes = Expressions.numberTemplate(Double.class, "log10({0} + 1)", likeCount);
        NumberExpression<Double> logViews = Expressions.numberTemplate(Double.class, "log10({0} + 1)", viewCount);
        NumberExpression<Double> logInterested = Expressions.numberTemplate(Double.class, "log10({0} + 1)", interestedCount);

        return averageStar.multiply(0.3)
            .add(logLikes.multiply(0.3))
            .add(logViews.multiply(0.3))
            .add(logInterested.multiply(0.1));
    }

}
