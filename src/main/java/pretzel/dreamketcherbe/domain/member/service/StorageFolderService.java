package pretzel.dreamketcherbe.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderResDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderResDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.exception.StorageFolderException;
import pretzel.dreamketcherbe.domain.member.exception.StorageFolderExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.member.repository.StorageFolderRepository;

@Service
@RequiredArgsConstructor
public class StorageFolderService {

    private final StorageFolderRepository storageFolderRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public CreateFolderResDto createFolder(final Long memberId, CreateFolderReqDto createFolderReqDto) {
        Member member = findByMemberId(memberId);
        StorageFolder storageFolder = StorageFolder.create(createFolderReqDto, member);
        storageFolderRepository.save(storageFolder);
        return CreateFolderResDto.of(storageFolder);
    }

    @Transactional
    public UpdateStorageFolderResDto updateFolder(final Long folderId, UpdateStorageFolderReqDto updateStorageFolderReqDto) {
        StorageFolder storageFolder = findByFolderId(folderId);
        storageFolder.update(updateStorageFolderReqDto);
        return UpdateStorageFolderResDto.of(storageFolder);
    }

    @Transactional
    public void deleteFolder(final Long folderId) {
        StorageFolder storageFolder = findByFolderId(folderId);
        storageFolder.delete();
    }

    private StorageFolder findByFolderId(final Long folderId) {
        return storageFolderRepository.findById(folderId)
            .orElseThrow(() -> new StorageFolderException(StorageFolderExceptionType.FOLDER_NOT_FOUND));
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
    }
}
