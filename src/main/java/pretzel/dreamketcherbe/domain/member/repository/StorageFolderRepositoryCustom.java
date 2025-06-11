package pretzel.dreamketcherbe.domain.member.repository;

import pretzel.dreamketcherbe.domain.member.dto.StorageFolderResDto;

public interface StorageFolderRepositoryCustom {

    StorageFolderResDto findAllStorageFolderWithPage(Long memberId);
}
