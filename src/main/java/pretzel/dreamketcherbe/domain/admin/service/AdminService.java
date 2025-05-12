package pretzel.dreamketcherbe.domain.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.admin.dto.AdminLogItemDto;
import pretzel.dreamketcherbe.domain.admin.dto.AdminLogResDto;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.ModerationLog;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;
import pretzel.dreamketcherbe.domain.admin.repository.ModerationLogRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ModerationLogRepository moderationLogRepository;

    /**
     * 관리 로그를 필터 및 페이지네이션 조건에 따라 조회하여 반환합니다.
     *
     * @param targetType   조회할 대상 타입 (선택)
     * @param actionType   조회할 액션 타입 (선택)
     * @param fromDate     조회 시작 날짜 (포함, 선택)
     * @param toDate       조회 종료 날짜 (포함, 선택)
     * @param pageable     페이지네이션 정보
     * @return 필터링 및 페이지네이션된 관리 로그 목록과 메타데이터를 포함한 DTO
     */
    public AdminLogResDto getLogs(
        TargetType targetType,
        ActionType actionType,
        LocalDate fromDate,
        LocalDate toDate,
        Pageable pageable
    ) {
        LocalDateTime from = (fromDate != null
            ? fromDate.atStartOfDay()
            : null
        );
        LocalDateTime to = (toDate != null
            ? toDate.atTime(23, 59, 59)
            : null
        );

        Page<ModerationLog> logsPage = moderationLogRepository.findWithFilters(
            targetType, actionType, from, to, pageable
        );

        List<AdminLogItemDto> logsItems = logsPage.getContent().stream()
            .map(log -> AdminLogItemDto.builder()
                .time(log.getCreatedAt())
                .adminId(log.getAdminId())
                .actionType(log.getActionType())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .reasonText(log.getReasonText())
                .adminNote(log.getAdminNote())
                .build())
            .toList();

        return AdminLogResDto.builder()
            .logs(logsItems)
            .total(logsPage.getTotalElements())
            .page(logsPage.getNumber())
            .size(logsPage.getSize())
            .build();
    }
}
