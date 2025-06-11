package pretzel.dreamketcherbe.domain.member.repository;

import static pretzel.dreamketcherbe.domain.member.entity.QStorageFolder.storageFolder;
import static pretzel.dreamketcherbe.domain.member.entity.QStorageItem.storageItem;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pretzel.dreamketcherbe.domain.member.dto.StorageFolderContentDto;
import pretzel.dreamketcherbe.domain.member.dto.StorageFolderResDto;
import pretzel.dreamketcherbe.domain.member.dto.StorageItemContentDto;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;

@RequiredArgsConstructor
public class StorageFolderRepositoryCustomImpl implements StorageFolderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public StorageFolderResDto findAllStorageFolderWithPage(Long memberId) {
        List<StorageFolderContentDto> content = getStorageFolders(memberId);
        long total = getTotalDataCount(memberId);
        return StorageFolderResDto.of(content, total);
    }

    private List<StorageFolderContentDto> getStorageFolders(Long memberId) {
        List<StorageFolder> folders = getFolders(memberId);
        List<StorageItem> items = getItems(folders);

        Map<Long, List<StorageItemContentDto>> itemMap = items.stream()
            .collect(Collectors.groupingBy(
                item -> item.getStorageFolder().getId(),
                Collectors.mapping(StorageItemContentDto::of, Collectors.toList())
            ));

        return folders.stream()
            .map(folder -> StorageFolderContentDto.of(
                folder,
                itemMap.getOrDefault(folder.getId(), Collections.emptyList())
            ))
            .toList();
    }

    private long getTotalDataCount(final Long memberId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(storageFolder.count())
                .from(storageFolder)
                .where(storageFolder.member.id.eq(memberId))
                .fetchOne()
            )
        .orElse(0L);
    }

    private List<StorageFolder> getFolders(final Long memberId) {
        return jpaQueryFactory
            .selectFrom(storageFolder)
            .where(storageFolder.member.id.eq(memberId))
            .fetch();
    }

    private List<StorageItem> getItems(List<StorageFolder> folders) {
        return jpaQueryFactory
            .selectFrom(storageItem)
            .where(storageItem.storageFolder.id.in(folders.stream().map(StorageFolder::getId).toList()))
            .fetch();
    }
}
