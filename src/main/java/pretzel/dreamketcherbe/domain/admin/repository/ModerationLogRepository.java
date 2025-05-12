package pretzel.dreamketcherbe.domain.admin.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.ModerationLog;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;

public interface ModerationLogRepository extends JpaRepository<ModerationLog, Long> {

    @Query("SELECT m FROM ModerationLog m WHERE " +
        "(:targetType IS NULL OR m.targetType = :targetType) AND " +
        "(:actionType IS NULL OR m.actionType = :actionType) AND " +
        "(:from IS NULL OR m.createdAt >= :from) AND " +
        "(:to IS NULL OR m.createdAt <= :to)")
    Page<ModerationLog> findWithFilters(
        @Param("targetType") TargetType targetType,
        @Param("actionType") ActionType actionType,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
}
