package pretzel.dreamketcherbe.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderResDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
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

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
    }
}
