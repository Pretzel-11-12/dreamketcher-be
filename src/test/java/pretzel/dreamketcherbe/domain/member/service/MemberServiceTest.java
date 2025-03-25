package pretzel.dreamketcherbe.domain.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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

    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
            .socialType(SocialType.GOOGLE)
            .socialId("123")
            .email("test@example.com")
            .name("Test User")
            .nickname("TestNick")
            .role(Role.MEMBER)
            .build();

        // 현재 이미지 URL은 기본 이미지와 다른 값으로 설정 (예: 기존 이미지 URL)
        member.updateImageUrl("http://example.com/old.png");

        // memberRepository.findById()가 호출되면 testMember 반환
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // memberRepository.save()는 전달된 인스턴스를 그대로 반환
        when(memberRepository.save(any(Member.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        // memberService의 defaultProfileImageUrl 필드에 테스트용 값을 주입
        ReflectionTestUtils.setField(memberService, "defaultProfileImageUrl",
            defaultProfileImageUrl);
    }

    // 1. 모든 프로필 데이터(닉네임, 이메일, 소개)가 null일 때, isDeleteImage=false이고 image가 null인 경우
    @Test
    void testUpdateProfileWithImage_AllProfileDataNull_NoImageProvided() {
        // UpdateProfileRequest의 필드가 null인 경우 (isDeleteImage는 false)
        UpdateProfileRequest profileRequest = new UpdateProfileRequest(null, null, null, false);

        memberService.updateProfileWithImage(1L, null, profileRequest);

        // 프로필 필드는 변경되지 않아야 함
        assertEquals("testnick", member.getNickname());
        // 기존 businessEmail가 없었으므로 그대로 null 또는 기존 값
        assertNull(member.getBusinessEmail());
        // shortIntroduction가 null인 경우 업데이트하지 않음
        assertNull(member.getShortIntroduction());
        // 이미지도 변경하지 않음
        assertEquals("currentImage.jpg", member.getImageUrl());
        verify(memberRepository).save(member);
    }

    // 2. 모든 프로필 데이터가 null일 때, image가 제공되어 업데이트되는 경우
    @Test
    void testUpdateProfileWithImage_AllProfileDataNull_WithImageProvided() {
        UpdateProfileRequest profileRequest = new UpdateProfileRequest(null, null, null, false);
        MultipartFile image = new MockMultipartFile("image", "filename.jpg", "image/jpeg",
            "dummy".getBytes());

        // s3Service.imageUpdate 성공 시 새로운 이미지 URL 반환
        when(s3Service.imageUpdate(eq("currentImage.jpg"), eq(image), eq("profile-images/1")))
            .thenReturn("updatedImage.jpg");

        memberService.updateProfileWithImage(1L, image, profileRequest);

        // 이미지 업데이트 결과 확인
        assertEquals("updatedImage.jpg", member.getImageUrl());
        verify(s3Service).imageUpdate("currentImage.jpg", image, "profile-images/1");
        verify(memberRepository).save(member);
    }

    // 3. 프로필 데이터(닉네임, 이메일, 짧은소개글)를 모두 업데이트하는 경우 (image는 null)
    @Test
    void testUpdateProfileWithImage_UpdateProfileFieldsOnly() {
        UpdateProfileRequest profileRequest = new UpdateProfileRequest(
            "newnick", "new short intro", "newbiz@example.com", false);
        // image가 제공되지 않음
        memberService.updateProfileWithImage(1L, null, profileRequest);

        // 각 필드가 업데이트되어야 함
        assertEquals("newnick", member.getNickname());
        assertEquals("newbiz@example.com", member.getBusinessEmail());
        assertEquals("new short intro", member.getShortIntroduction());
        // 이미지 변경은 없으므로 기존 값 유지
        assertEquals("currentImage.jpg", member.getImageUrl());
        verify(memberRepository).save(member);
    }

    // 4. 이미지 삭제 요청(isDeleteImage true) 시, 현재 이미지가 기본 이미지가 아닐 경우
    @Test
    void testUpdateProfileWithImage_DeleteImage_WhenCurrentImageNotDefault() {
        UpdateProfileRequest profileRequest = new UpdateProfileRequest("testnick", null, null,
            true);

        memberService.updateProfileWithImage(1L, null, profileRequest);

        // s3Service.deleteImage가 호출되고, 멤버의 이미지가 기본 이미지로 업데이트되어야 함
        verify(s3Service).deleteImage("currentImage.jpg");
        assertEquals(defaultProfileImageUrl, member.getImageUrl());
        verify(memberRepository).save(member);
    }

    // 5. 이미지 삭제 요청(isDeleteImage true) 시, 이미 현재 이미지가 기본 이미지인 경우
    @Test
    void testUpdateProfileWithImage_DeleteImage_WhenAlreadyDefault() {
        // 초기 이미지를 기본 이미지로 설정
        member.updateImageUrl(defaultProfileImageUrl);
        UpdateProfileRequest profileRequest = new UpdateProfileRequest("testnick", null, null,
            true);

        memberService.updateProfileWithImage(1L, null, profileRequest);

        // 삭제 호출이 없어야 함
        verify(s3Service, never()).deleteImage(anyString());
        assertEquals(defaultProfileImageUrl, member.getImageUrl());
        verify(memberRepository).save(member);
    }

    // 6. 이미지 업데이트 시, 기존 이미지 업데이트에 실패하여 업로드(fallback)로 처리되는 경우
    @Test
    void testUpdateProfileWithImage_ImageUpdateFailureFallback() {
        UpdateProfileRequest profileRequest = new UpdateProfileRequest(null, null, null, false);
        MultipartFile image = new MockMultipartFile("image", "filename.jpg", "image/jpeg",
            "dummy".getBytes());

        // imageUpdate에서 예외 발생
        when(s3Service.imageUpdate(eq("currentImage.jpg"), eq(image), eq("profile-images/1")))
            .thenThrow(new RuntimeException("Update failed"));
        // imageUpload의 경우 새로운 이미지 URL 반환
        when(s3Service.imageUpload(eq(image), eq("profile-images/1")))
            .thenReturn("uploadedImage.jpg");

        memberService.updateProfileWithImage(1L, image, profileRequest);

        // fallback으로 업로드된 URL이 반영되어야 함
        assertEquals("uploadedImage.jpg", member.getImageUrl());
        verify(s3Service).imageUpdate("currentImage.jpg", image, "profile-images/1");
        verify(s3Service).imageUpload(image, "profile-images/1");
        verify(memberRepository).save(member);
    }

    // 7. 부분적으로 업데이트하는 경우 (예: 닉네임만 null이고 나머지 변경)
    @Test
    void testUpdateProfileWithImage_PartialUpdate() {
        // 기존 필드: nickname="testnick", businessEmail=null, shortIntroduction=null
        UpdateProfileRequest profileRequest = new UpdateProfileRequest(
            null, "updated intro", "updatedbiz@example.com", false);

        memberService.updateProfileWithImage(1L, null, profileRequest);

        // nickname은 null이므로 변경되지 않고, 나머지 필드는 업데이트 되어야 함
        assertEquals("testnick", member.getNickname());
        assertEquals("updatedbiz@example.com", member.getBusinessEmail());
        assertEquals("updated intro", member.getShortIntroduction());
        verify(memberRepository).save(member);
    }
}