package pretzel.dreamketcherbe.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.member.dto.*;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<SelfInfoResponse> me(@Auth Long memberId) {
        return ResponseEntity.ok(memberService.getSelfInfo(memberId));
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfileWithImage(
        @Auth Long memberId,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @RequestPart(value = "profileData", required = false) @Valid UpdateProfileRequest profileData
    ) {
        memberService.updateProfileWithImage(memberId, image, profileData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/favorite/{WebtoonId}")
    public ResponseEntity<InterestedWebtoonSimpleResponse> getFavoriteWebtoon(
        @Auth Long memberId,
        @PathVariable Long WebtoonId) {
        return ResponseEntity.ok(memberService.getFavoriteWebtoon(memberId, WebtoonId));
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<InterestedWebtoonResponse>> getAllFavoriteWebtoon(
        @Auth Long memberId) {
        return ResponseEntity.ok(memberService.getAllFavoriteWebtoon(memberId));
    }

    @DeleteMapping("/favorite/{WebtoonId}")
    public ResponseEntity<Void> deleteFavoriteWebtoon(@Auth Long memberId,
        @PathVariable Long WebtoonId) {
        memberService.deleteFavoriteWebtoon(memberId, WebtoonId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/works")
    public ResponseEntity<PageResDto<WorkResDto>> getAllWorks(@Auth Long memberId,
        @RequestParam(defaultValue = "all") String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size) {
        PageReqDto pageReqDto = PageReqDto.of(page, size);
        return ResponseEntity.ok(memberService.getAllWorks(memberId, status, pageReqDto));
    }
}
