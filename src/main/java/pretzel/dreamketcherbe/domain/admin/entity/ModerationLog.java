package pretzel.dreamketcherbe.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;

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

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = true)
    private ReportReason reason;

    /**
     * 신고자 자유 입력 사항
     */
    @Column(name = "reason_text", columnDefinition = "TEXT")
    private String reasonText;

    /**
     * 관리자 추가 메모
     */
    @Column(name = "admin_note", length = 255)
    private String adminNote;

    public static ModerationLog of(
        Long adminId,
        ActionType actionType,
        TargetType targetType,
        Long targetId,
        ReportReason reason,
        String reasonText,
        String adminNote
    ) {
        return ModerationLog.builder()
            .adminId(adminId)
            .actionType(actionType)
            .targetType(targetType)
            .targetId(targetId)
            .reason(reason)
            .reasonText(reasonText)
            .adminNote(adminNote)
            .build();
    }
}
