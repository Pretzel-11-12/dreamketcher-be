package pretzel.dreamketcherbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

public interface StorageItemRepository extends JpaRepository<StorageItem, Long>, StorageItemRepositoryCustom {

    boolean existsByStorageFolderAndWebtoon(StorageFolder folder, Webtoon webtoon);
}
