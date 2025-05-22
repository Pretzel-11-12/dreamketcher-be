package pretzel.dreamketcherbe.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Admin;
import pretzel.dreamketcherbe.domain.admin.dto.MemberListResponseDto;
import pretzel.dreamketcherbe.domain.admin.dto.MemberSuspendRequestDto;
import pretzel.dreamketcherbe.domain.admin.service.ManageMemberService;
import pretzel.dreamketcherbe.domain.member.entity.MemberStatus;

@RestController
@RequestMapping("/api/v1/admin/member")
@RequiredArgsConstructor
public class ManageMemberController {

    private final ManageMemberService memberManagementService;

    /**
     * 관리자가 회원 상태와 페이지 정보를 기준으로 회원 목록을 조회합니다.
     *
     * @param status 조회할 회원 상태 (선택 사항, ACTIVE 또는 SUSPENDED)
     * @param page 조회할 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 회원 수 (기본값: 20)
     * @return 회원 목록 및 페이징 정보가 포함된 응답
     */
    @GetMapping
    public ResponseEntity<MemberListResponseDto> getMembers(
        @Admin Long adminId,
        @RequestParam(required = false) MemberStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        MemberListResponseDto response = memberManagementService.getMembers(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 지정한 회원을 일정 기간 동안 정지시킵니다.
     *
     * 관리자가 회원의 ID와 정지 사유 및 기간을 입력하면 해당 회원을 정지 처리합니다.
     *
     * @param memberId 정지할 회원의 식별자
     * @param request 정지 기간과 사유가 포함된 요청 객체
     * @return 성공 시 HTTP 200 OK 응답
     */
    @PostMapping("/{memberId}/suspend")
    public ResponseEntity<Void> suspendMember(
        @Admin Long adminId,
        @PathVariable Long memberId,
        @Valid @RequestBody MemberSuspendRequestDto request
    ) {
        memberManagementService.suspendMember(adminId, memberId, request);
        return ResponseEntity.ok().build();
    }

    /****
     * 관리자가 특정 회원의 정지를 해제하는 API입니다.
     *
     * @param memberId 정지 해제할 회원의 ID
     * @return HTTP 200 OK 응답
     */
    @PostMapping("/{memberId}/activate")
    public ResponseEntity<Void> activateMember(
        @Admin Long adminId,
        @PathVariable Long memberId
    ) {
        memberManagementService.activateMember(adminId, memberId);
        return ResponseEntity.ok().build();
    }
}
