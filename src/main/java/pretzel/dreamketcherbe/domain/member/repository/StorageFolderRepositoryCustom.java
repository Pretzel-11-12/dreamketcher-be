package pretzel.dreamketcherbe.domain.member.repository;

import pretzel.dreamketcherbe.domain.member.dto.StorageFolderResDto;

public interface StorageFolderRepositoryCustom {

    StorageFolderResDto findAllStorageFolderWithPage(Long memberId);

    // 공개 폴더만 (isPrivated=false)
    StorageFolderResDto findAllPublicStorageFolderWithPage(Long memberId);
}
