package pretzel.dreamketcherbe.domain.member.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.member.dto.NicknameRequest;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.service.MemberService;

import java.util.List;

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
    public ResponseEntity<Void> updateProfile(@RequestParam Long memberId, @Valid @RequestBody NicknameRequest nicknameRequest) {
        log.info("memberId: {}, nicknameRequest: {}", memberId, nicknameRequest);
        memberService.updateProfile(memberId, nicknameRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        log.info("members: {}", members);
        return ResponseEntity.ok(members);
    }
}
