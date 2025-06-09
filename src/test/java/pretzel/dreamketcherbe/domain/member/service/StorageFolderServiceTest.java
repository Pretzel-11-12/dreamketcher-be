package pretzel.dreamketcherbe.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pretzel.dreamketcherbe.domain.fixture.FixtureFactory;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderResDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderResDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.member.repository.StorageFolderRepository;

@ExtendWith(MockitoExtension.class)
class StorageFolderServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StorageFolderRepository storageFolderRepository;

    @InjectMocks
    private StorageFolderService storageFolderService;

    @Nested
    @DisplayName("폴더 생성 테스트")
    class CreateStorageFolderTests {

        @Test
        @DisplayName("폴더 생성 성공 테스트")
        void 폴더_생성_성공_테스트() {
            // given
            Long memberId = 1L;
            CreateFolderReqDto reqDto = new CreateFolderReqDto("폴더 이름", false);

            Member member = FixtureFactory.getMember(memberId);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            StorageFolder storageFolder = StorageFolder.create(reqDto, member);
            given(storageFolderRepository.save(any(StorageFolder.class))).willReturn(storageFolder);

            // when
            CreateFolderResDto result = storageFolderService.createFolder(memberId, reqDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.folderName()).isEqualTo("폴더 이름");
        }
    }

    @Nested
    @DisplayName("폴더 수정 테스트")
    class UpdateStorageFolderTests {
        @Test
        @DisplayName("폴더 수정 성공 테스트")
        void 폴더_수정_성공_테스트() {
            // given
            Long folderId = 1L;

            StorageFolder storageFolder = FixtureFactory.getStorageFolder(folderId);
            UpdateStorageFolderReqDto updateDto = new UpdateStorageFolderReqDto("새 폴더명");

            given(storageFolderRepository.findById(folderId))
                .willReturn(Optional.of(storageFolder));

            // when
            UpdateStorageFolderResDto result = storageFolderService.updateFolder(folderId, updateDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.folderName()).isEqualTo(updateDto.folderName());
        }
    }

    @Nested
    @DisplayName("폴더 삭제 테스트")
    class DeleteStorageFolderTests {
        @Test
        @DisplayName("폴더 삭제 성공 테스트")
        void 폴더_삭제_성공_테스트() {
            // given
            Long folderId = 1L;

            StorageFolder storageFolder = FixtureFactory.getStorageFolder(folderId);

            given(storageFolderRepository.findById(folderId))
                .willReturn(Optional.of(storageFolder));

            // when
            storageFolderService.deleteFolder(folderId);

            // then
            assertThat(storageFolderRepository.existsById(folderId)).isFalse();
        }
    }
}