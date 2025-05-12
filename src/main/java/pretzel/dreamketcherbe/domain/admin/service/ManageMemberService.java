package pretzel.dreamketcherbe.domain.admin.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.admin.dto.MemberListResponseDto;
import pretzel.dreamketcherbe.domain.admin.dto.MemberSuspendRequestDto;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.MemberStatus;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.repository.ReportReasonRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageMemberService {

    private final MemberRepository memberRepository;
    private final ModerationEventPublisher moderationEventPublisher;
    private final ReportReasonRepository reportReasonRepository;

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

        // ReportReason 객체 조회
        ReportReason reportReason = reportReasonRepository
            .findByCode(suspendRequestDto.reasonCode())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정지 사유 코드입니다"));

        // 정지 사유 메시지 생성
        String adminNote = suspendRequestDto.reasonCode().getDefaultDescription();
        if (suspendRequestDto.suspensionReason() != null && !suspendRequestDto.suspensionReason()
            .isEmpty()) {
            adminNote += " - " + suspendRequestDto.suspensionReason();
        }

        // 회원 정지 처리
        member.suspend(suspendedUntil, suspendRequestDto.suspensionReason());

        // 관리자 로그 이벤트 발행
        moderationEventPublisher.publishModerationLogEvent(
            adminId,
            ActionType.USER_SUSPENDED,
            TargetType.USER,
            memberId,
            reportReason,
            null, // 신고자 사유
            "정지일수: " + suspendRequestDto.suspensionDays() + "일, 사유: "
                + adminNote
        );
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

        // 관리자 로그 이벤트 발행
        log.info("회원 정지 해제 이벤트 발행 시작: adminId={}, memberId={}", adminId, memberId);
        moderationEventPublisher.publishModerationLogEvent(
            adminId,
            ActionType.USER_ACTIVATED,
            TargetType.USER,
            memberId,
            null, // 특정 신고 사유가 없으므로 null
            null, // 특정 신고 내용이 없으므로 null
            "정지 해제"
        );
        log.info("회원 정지 해제 이벤트 발행 완료: adminId={}, memberId={}", adminId, memberId);
    }
}
