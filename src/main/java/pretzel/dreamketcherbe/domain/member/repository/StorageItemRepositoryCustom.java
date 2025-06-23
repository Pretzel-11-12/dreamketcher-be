package pretzel.dreamketcherbe.domain.member.repository;

import pretzel.dreamketcherbe.domain.member.dto.StorageItemResDto;

public interface StorageItemRepositoryCustom {

    StorageItemResDto findAllStorageItemWithPage(Long memberId, Long folderId);

    // 공개 폴더 접근 시 (memberId 없이)
    StorageItemResDto findAllStorageItemWithPagePublic(Long folderId);

}
