package pretzel.dreamketcherbe.domain.admin.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.admin.dto.MemberListResponseDto;
import pretzel.dreamketcherbe.domain.admin.dto.MemberSuspendRequestDto;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.ModerationLog;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;
import pretzel.dreamketcherbe.domain.admin.repository.ModerationLogRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.MemberStatus;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberManagementService {

    private final MemberRepository memberRepository;
    private final ModerationLogRepository moderationLogRepository;

    /**
     * 회원 목록 조회
     *
     * @param status   회원 상태 필터 (활성/정지)
     * @param pageable 페이징 정보
     * @return 회원 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public MemberListResponseDto getMembers(MemberStatus status, Pageable pageable) {
        Page<Member> members;

        if (status != null) {
            members = memberRepository.findAllByStatus(status, pageable);
        } else {
            members = memberRepository.findAll(pageable);
        }

        return MemberListResponseDto.from(members);
    }

    /**
     * 회원 정지
     *
     * @param adminId           관리자 ID
     * @param memberId          정지할 회원 ID
     * @param suspendRequestDto 정지 요청 DTO
     */
    @Transactional
    public void suspendMember(Long adminId, Long memberId,
        MemberSuspendRequestDto suspendRequestDto) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        // 이미 정지된 회원인지 확인
        if (member.getStatus() == MemberStatus.SUSPENDED) {
            throw new MemberException(MemberExceptionType.ALREADY_SUSPENDED);
        }

        // 정지 기간 계산
        LocalDateTime suspendedUntil = LocalDateTime.now()
            .plusDays(suspendRequestDto.suspensionDays());

        // 회원 정지 처리
        member.suspend(suspendedUntil, suspendRequestDto.suspensionReason());

        // 관리자 로그 기록
        ModerationLog log = ModerationLog.builder()
            .adminId(adminId)
            .targetId(memberId)
            .targetType(TargetType.USER)
            .actionType(ActionType.USER_SUSPENDED)
            .adminNote("정지일수: " + suspendRequestDto.suspensionDays() + "일, 사유: "
                + suspendRequestDto.suspensionReason())
            .build();

        moderationLogRepository.save(log);
    }

    /**
     * 회원 정지 해제
     *
     * @param adminId  관리자 ID
     * @param memberId 정지 해제할 회원 ID
     */
    @Transactional
    public void activateMember(Long adminId, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        // 정지된 회원이 아닌 경우 예외 처리
        if (member.getStatus() != MemberStatus.SUSPENDED) {
            throw new MemberException(MemberExceptionType.NOT_SUSPENDED);
        }

        // 회원 활성화 처리
        member.activate();

        // 관리자 로그 기록
        ModerationLog log = ModerationLog.builder()
            .adminId(adminId)
            .targetId(memberId)
            .targetType(TargetType.USER)
            .actionType(ActionType.USER_ACTIVATED)
            .adminNote("정지 해제")
            .build();

        moderationLogRepository.save(log);
    }
}
