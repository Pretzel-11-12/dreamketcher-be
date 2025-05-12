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
     * 주어진 회원 상태에 따라 회원 목록을 페이징하여 조회한다.
     *
     * @param status 조회할 회원 상태. null이면 모든 회원을 조회한다.
     * @param pageable 페이징 및 정렬 정보
     * @return 조회된 회원 목록을 담은 응답 DTO
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
     * 지정한 회원을 요청된 기간 동안 정지 처리합니다.
     *
     * 회원이 존재하지 않거나 이미 정지된 경우 예외를 발생시킵니다. 정지 사유 코드를 기반으로 사유를 조회하고, 추가 사유가 있으면 함께 기록합니다. 정지 처리 후 관리자 로그 이벤트를 발행합니다.
     *
     * @param adminId 정지 처리를 수행하는 관리자 ID
     * @param memberId 정지 대상 회원의 ID
     * @param suspendRequestDto 정지 기간, 사유 코드, 추가 사유가 포함된 요청 DTO
     * @throws MemberException 회원이 존재하지 않거나 이미 정지된 경우
     * @throws IllegalArgumentException 유효하지 않은 정지 사유 코드인 경우
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
     * 정지된 회원의 계정을 활성화합니다.
     *
     * 주어진 회원 ID에 해당하는 회원이 정지 상태일 때만 정지 해제를 수행하며, 관리 로그 이벤트를 발행합니다.
     *
     * @param adminId 정지 해제를 수행하는 관리자 ID
     * @param memberId 정지 해제할 회원의 ID
     * @throws MemberException 회원이 존재하지 않거나 정지 상태가 아닌 경우 발생
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
