package pretzel.dreamketcherbe.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.member.dto.NicknameRequest;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<SelfInfoResponse> me(@Auth Long memberId) {
        log.info("memberId: {}", memberId);
        return ResponseEntity.ok(memberService.getSelfInfo(memberId));
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestParam Long memberId, @RequestBody NicknameRequest nicknameRequest) {
        memberService.updateProfile(memberId, nicknameRequest);
        return ResponseEntity.ok().build();
    }
}
