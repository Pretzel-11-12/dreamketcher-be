package pretzel.dreamketcherbe.domain.member.repository;

import pretzel.dreamketcherbe.domain.member.dto.StorageItemResDto;

public interface StorageItemRepositoryCustom {

    StorageItemResDto findAllStorageItemWithPage(Long memberId, Long folderId);

}
