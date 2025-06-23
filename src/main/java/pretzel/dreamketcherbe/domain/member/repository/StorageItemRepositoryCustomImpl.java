package pretzel.dreamketcherbe.domain.member.repository;

import static pretzel.dreamketcherbe.domain.member.entity.QStorageFolder.storageFolder;
import static pretzel.dreamketcherbe.domain.member.entity.QStorageItem.storageItem;
import static pretzel.dreamketcherbe.domain.webtoon.entity.QWebtoon.webtoon;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import pretzel.dreamketcherbe.domain.member.dto.StorageItemContentDto;
import pretzel.dreamketcherbe.domain.member.dto.StorageItemResDto;

@RequiredArgsConstructor
public class StorageItemRepositoryCustomImpl implements StorageItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public StorageItemResDto findAllStorageItemWithPage(Long memberId, Long folderId) {
        List<StorageItemContentDto> content = getStorageItems(memberId, folderId);
        long total = getTotalDataCount(memberId, folderId);
        String folderName = getFolderName(folderId);
        return StorageItemResDto.of(folderId, folderName, content, total);
    }

    @Override
    public StorageItemResDto findAllStorageItemWithPagePublic(Long folderId) {
        List<StorageItemContentDto> content = getStorageItemsPublic(folderId);
        long total = getTotalDataCountPublic(folderId);
        String folderName = getFolderName(folderId);
        return StorageItemResDto.of(folderId, folderName, content, total);
    }

    private List<StorageItemContentDto> getStorageItems(Long memberId, Long folderId) {
        return jpaQueryFactory.select(
            Projections.constructor(pretzel.dreamketcherbe.domain.member.dto.StorageItemContentDto.class,
                webtoon.id,
                webtoon.title,
                webtoon.thumbnail,
                webtoon.member.nickname
            ))
            .from(storageItem)
            .join(storageItem.webtoon, webtoon)
            .where(getWhereConditions(memberId, folderId))
            .fetch();
    }

    private List<StorageItemContentDto> getStorageItemsPublic(Long folderId) {
        return jpaQueryFactory.select(
                Projections.constructor(pretzel.dreamketcherbe.domain.member.dto.StorageItemContentDto.class,
                    webtoon.id,
                    webtoon.title,
                    webtoon.thumbnail,
                    webtoon.member.nickname
                ))
            .from(storageItem)
            .join(storageItem.webtoon, webtoon)
            .where(storageItem.storageFolder.id.eq(folderId))
            .fetch();
    }

    private long getTotalDataCount(Long memberId, Long folderId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(storageItem.count())
                .from(storageItem)
                .join(storageItem.webtoon, webtoon)
                .where(getWhereConditions(memberId, folderId))
                .fetchOne()
            )
            .orElse(0L);
    }

    private long getTotalDataCountPublic(Long folderId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(storageItem.count())
                .from(storageItem)
                .where(storageItem.storageFolder.id.eq(folderId))
                .fetchOne())
            .orElse(0L);
    }

    private BooleanBuilder getWhereConditions(Long memberId, Long folderId) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(storageItem.member.id.eq(memberId));
        builder.and(storageItem.storageFolder.id.eq(folderId));

        return builder;
    }

    private String getFolderName(Long folderId) {
        return jpaQueryFactory.select(
            storageFolder.name
            )
            .from(storageFolder)
            .where(storageFolder.id.eq(folderId))
            .fetchOne();
    }
}
