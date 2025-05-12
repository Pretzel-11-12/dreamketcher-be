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
