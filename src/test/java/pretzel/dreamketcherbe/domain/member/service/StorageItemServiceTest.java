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
import pretzel.dreamketcherbe.domain.member.dto.CreateStorageItemReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateStorageItemResDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.member.repository.StorageFolderRepository;
import pretzel.dreamketcherbe.domain.member.repository.StorageItemRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@ExtendWith(MockitoExtension.class)
class StorageItemServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StorageFolderRepository storageFolderRepository;

    @Mock
    private WebtoonRepository webtoonRepository;

    @Mock
    private StorageItemRepository storageItemRepository;

    @InjectMocks
    private StorageItemService storageItemService;

    @Nested
    @DisplayName("아이템 생성 테스트")
    class CreateStorageItemTests {

        @Test
        @DisplayName("아이템 생성 성공 테스트")
        void 아이템_생성_성공_테스트() {
            // given
            Long memberId = 1L;
            Long folderId = 10L;
            Long webtoonId = 100L;

            Member member = FixtureFactory.getMember(memberId);
            StorageFolder folder = FixtureFactory.getStorageFolder(folderId);
            Webtoon webtoon = FixtureFactory.getWebtoon(webtoonId, member);

            StorageItem item = StorageItem.create(folder, member, webtoon);

            CreateStorageItemReqDto reqDto = new CreateStorageItemReqDto(webtoonId);

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(storageFolderRepository.findById(folderId)).willReturn(Optional.of(folder));
            given(webtoonRepository.findById(webtoonId)).willReturn(Optional.of(webtoon));
            given(storageItemRepository.save(any(StorageItem.class))).willReturn(item);

            // when
            CreateStorageItemResDto result = storageItemService.createItem(memberId, folderId, reqDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.StorageItemId()).isEqualTo(item.getId());
        }
    }
}