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
     * 회원 목록 조회 API
     *
     * @param adminId 관리자 ID (@Admin 어노테이션을 통해 주입)
     * @param status  회원 상태 필터 (ACTIVE: 활성화, SUSPENDED: 정지)
     * @param page    페이지 번호 (기본값: 0)
     * @param size    페이지 크기 (기본값: 20)
     * @return 회원 목록
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
     * 회원 정지 API
     *
     * @param adminId  관리자 ID (@Admin 어노테이션을 통해 주입)
     * @param memberId 정지할 회원 ID
     * @param request  정지 요청 정보 (정지 일수, 정지 사유)
     * @return 성공 응답
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

    /**
     * 회원 정지 해제 API
     *
     * @param adminId  관리자 ID (@Admin 어노테이션을 통해 주입)
     * @param memberId 정지 해제할 회원 ID
     * @return 성공 응답
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
