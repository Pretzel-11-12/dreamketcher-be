package pretzel.dreamketcherbe.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.member.dto.CreateStorageItemReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateStorageItemResDto;
import pretzel.dreamketcherbe.domain.member.dto.StorageItemResDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.exception.StorageFolderException;
import pretzel.dreamketcherbe.domain.member.exception.StorageFolderExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.member.repository.StorageFolderRepository;
import pretzel.dreamketcherbe.domain.member.repository.StorageItemRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Service
@RequiredArgsConstructor
public class StorageItemService {

    private final StorageItemRepository storageItemRepository;
    private final StorageFolderRepository storageFolderRepository;
    private final MemberRepository memberRepository;
    private final WebtoonRepository webtoonRepository;

    public StorageItemResDto getItems(final Long memberId, final Long folderId) {
        return storageItemRepository.findAllStorageItemWithPage(memberId, folderId);
    }

    // 공개 폴더 조회 (memberId 없이)
    public StorageItemResDto getPublicItems(final Long folderId) {
        StorageFolder folder = findByFolderId(folderId);
        if (folder.isPrivate()) {
            throw new StorageFolderException(StorageFolderExceptionType.PRIVATED_FOLDER_FORBIDDEN);
        }
        return storageItemRepository.findAllStorageItemWithPagePublic(folderId);
    }

    public CreateStorageItemResDto createItem(
        final Long memberId,
        final Long folderId,
        CreateStorageItemReqDto createStorageItemReqDto
    ) {
        Member member = findByMemberId(memberId);
        StorageFolder folder = findByFolderId(folderId);
        Webtoon webtoon = findByWebtoonId(createStorageItemReqDto.webtoonId());
        checkWebtoonInFolder(folder, webtoon);
        StorageItem item = StorageItem.create(folder, member, webtoon);
        return CreateStorageItemResDto.of(storageItemRepository.save(item));
    }

    public void deleteItem(final Long id) {
        StorageItem item = findByItemId(id);
        storageItemRepository.delete(item);
    }

    private StorageItem findByItemId(final Long itemId) {
        return storageItemRepository.findById(itemId)
            .orElseThrow(
                () -> new StorageFolderException(StorageFolderExceptionType.ITEM_NOT_FOUND));
    }

    private Webtoon findByWebtoonId(final Long webtoonId) {
        return webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));
    }

    private StorageFolder findByFolderId(final Long folderId) {
        return storageFolderRepository.findById(folderId)
            .orElseThrow(
                () -> new StorageFolderException(StorageFolderExceptionType.FOLDER_NOT_FOUND));
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
    }

    private void checkWebtoonInFolder(StorageFolder folder, Webtoon webtoon) {
        if (storageItemRepository.existsByStorageFolderAndWebtoon(folder, webtoon)) {
            throw new StorageFolderException(StorageFolderExceptionType.DUPLICATE_ITEM);
        }
    }
}
