package pretzel.dreamketcherbe.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Entity
@Getter
@Table(name = "moderation_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ModerationLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /**
     * 주어진 정보로 ModerationLog 인스턴스를 생성합니다.
     *
     * @param actionType 수행된 모더레이션 작업의 유형
     * @param targetType 모더레이션 대상의 유형
     * @param detail 모더레이션 이벤트에 대한 상세 정보
     * @param targetId 모더레이션 대상의 식별자
     * @return 생성된 ModerationLog 객체
     */
    public static ModerationLog of(ActionType actionType, TargetType targetType, String detail,
        Long targetId) {
        return ModerationLog.builder()
            .actionType(actionType)
            .targetType(targetType)
            .detail(detail)
            .targetId(targetId)
            .build();
    }
}
