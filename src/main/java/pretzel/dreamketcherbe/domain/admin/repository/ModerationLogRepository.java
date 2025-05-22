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

    /**
     * 주어진 필터 조건에 따라 모더레이션 로그 목록을 페이지 단위로 조회합니다.
     *
     * <p>타겟 유형, 액션 유형, 생성일 범위(from~to) 중 null이 아닌 값만 필터로 적용되며,
     * 결과는 페이지네이션 및 정렬이 가능합니다.</p>
     *
     * @param targetType   필터링할 타겟 유형 (null이면 필터 미적용)
     * @param actionType   필터링할 액션 유형 (null이면 필터 미적용)
     * @param from         조회 시작일시 (null이면 필터 미적용)
     * @param to           조회 종료일시 (null이면 필터 미적용)
     * @param pageable     페이지네이션 및 정렬 정보
     * @return 필터 조건에 맞는 모더레이션 로그의 페이지 결과
     */
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
