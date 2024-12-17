package pretzel.dreamketcherbe.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.member.dto.InterestedWebtoonResponse;
import pretzel.dreamketcherbe.domain.member.dto.NicknameRequest;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
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
  public ResponseEntity<Void> updateProfile(@Auth Long memberId,
      @Valid @RequestBody NicknameRequest nicknameRequest) {
    memberService.updateProfile(memberId, nicknameRequest);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/all")
  public ResponseEntity<List<Member>> getAllMembers() {
    List<Member> members = memberService.getAllMembers();
    return ResponseEntity.ok(members);
  }

  @GetMapping("/favorite")
  public ResponseEntity<List<InterestedWebtoonResponse>> getFavoriteWebtoon(
      @Auth Long memberId) {
    return ResponseEntity.ok(memberService.getFavoriteWebtoon(memberId));
  }

  @DeleteMapping("/favorite/{InterestedWebtoonId}")
  public ResponseEntity<Void> deleteFavoriteWebtoon(@Auth Long memberId,
      @PathVariable Long InterestedWebtoonId) {
    memberService.deleteFavoriteWebtoon(memberId, InterestedWebtoonId);
    return ResponseEntity.ok().build();
  }
}
