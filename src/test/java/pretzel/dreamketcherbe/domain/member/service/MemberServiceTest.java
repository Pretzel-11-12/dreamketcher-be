package pretzel.dreamketcherbe.domain.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.domain.member.dto.UpdateProfileRequest;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.entity.SocialType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MemberService memberService;

    // 기본 프로필 이미지 URL (실제 @Value로 주입되지만 테스트에서는 ReflectionTestUtils로 주입)
    private final String defaultProfileImageUrl = "http://example.com/default.png";

    private Member testMember;

    @BeforeEach
    public void setUp() {
        testMember = Member.builder()
            .socialType(SocialType.GOOGLE)
            .socialId("123")
            .email("test@example.com")
            .name("Test User")
            .nickname("TestNick")
            .role(Role.MEMBER)
            .build();

        // 현재 이미지 URL은 기본 이미지와 다른 값으로 설정 (예: 기존 이미지 URL)
        testMember.updateImageUrl("http://example.com/old.png");

        // memberRepository.findById()가 호출되면 testMember 반환
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        // memberRepository.save()는 전달된 인스턴스를 그대로 반환
        when(memberRepository.save(any(Member.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        // memberService의 defaultProfileImageUrl 필드에 테스트용 값을 주입
        ReflectionTestUtils.setField(memberService, "defaultProfileImageUrl",
            defaultProfileImageUrl);
    }

    /**
     * 케이스 1: 프로필 데이터의 isDeleteImage가 true이면, S3Service.deleteImage 호출 후 기본 이미지 URL로 업데이트
     */
    @Test
    public void testUpdateProfileWithImage_deleteImage() {
        UpdateProfileRequest request = new UpdateProfileRequest(
            "NewNick", "New short intro", "new@example.com", true
        );

        memberService.updateProfileWithImage(1L, null, request);

        verify(s3Service, times(1)).deleteImage("http://example.com/old.png");
        assertEquals(defaultProfileImageUrl, testMember.getImageUrl());

        assertEquals("NewNick", testMember.getNickname());
        assertEquals("new@example.com", testMember.getBusinessEmail());
        assertEquals("New short intro", testMember.getShortIntroduction());
    }

    /**
     * 케이스 2: isDeleteImage가 false이고 이미지 파일이 제공된 경우.
     */
    @Test
    public void testUpdateProfileWithImage_updateImage() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
            "UpdatedNick", "Updated intro", "updated@example.com", false
        );

        MultipartFile image = new MockMultipartFile("image", "test.png", "image/png",
            "dummy image content".getBytes());

        when(s3Service.imageUpdate(eq("http://example.com/old.png"), eq(image), anyString()))
            .thenReturn("http://example.com/updated.png");

        memberService.updateProfileWithImage(1L, image, request);

        verify(s3Service, times(1)).imageUpdate(eq("http://example.com/old.png"), eq(image),
            anyString());
        verify(s3Service, never()).imageUpload(any(MultipartFile.class), anyString());

        assertEquals("http://example.com/updated.png", testMember.getImageUrl());
        assertEquals("UpdatedNick", testMember.getNickname());
        assertEquals("updated@example.com", testMember.getBusinessEmail());
        assertEquals("Updated intro", testMember.getShortIntroduction());
    }

    /**
     * 케이스 3: 프로필 데이터 업데이트만 진행(이미지 파일 없이 isDeleteImage가 false인 경우)
     */
    @Test
    public void testUpdateProfileWithImage_noImageNoDelete() {
        UpdateProfileRequest request = new UpdateProfileRequest(
            "NickNoImage", "IntroNoImage", "noimage@example.com", false
        );
        memberService.updateProfileWithImage(1L, null, request);

        verify(s3Service, never()).deleteImage(anyString());
        verify(s3Service, never()).imageUpdate(anyString(), any(MultipartFile.class), anyString());
        verify(s3Service, never()).imageUpload(any(MultipartFile.class), anyString());

        assertEquals("http://example.com/old.png", testMember.getImageUrl());
        assertEquals("NickNoImage", testMember.getNickname());
        assertEquals("noimage@example.com", testMember.getBusinessEmail());
        assertEquals("IntroNoImage", testMember.getShortIntroduction());
    }
}