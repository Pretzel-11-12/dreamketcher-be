package pretzel.dreamketcherbe.domain.report.entity;


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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Entity
@Table(name = "episode_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EpisodeReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    @Column(name = "reporter_member_id")
    private Long reporterMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = false)
    private ReportReason reason;

    @Column(name = "reason_text", columnDefinition = "TEXT")
    private String reasonText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReportStatus status;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_note", length = 255)
    private String adminNote;


    /**
     * 회원이 에피소드를 신고할 때 새로운 EpisodeReport 인스턴스를 생성합니다.
     *
     * @param episodeId 신고 대상 에피소드의 ID
     * @param memberId 신고자 회원의 ID
     * @param reason 신고 사유 엔티티
     * @param reasonText 신고 상세 사유(선택)
     * @return 생성된 EpisodeReport 객체
     */
    public static EpisodeReport forMember(
        Long episodeId,
        Long memberId,
        ReportReason reason,
        String reasonText
    ) {
        return EpisodeReport.builder()
            .episodeId(episodeId)
            .reporterMemberId(memberId)
            .reason(reason)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    /**
     * 비회원(게스트)이 에피소드를 신고할 때 새로운 EpisodeReport 인스턴스를 생성합니다.
     *
     * @param episodeId 신고 대상 에피소드의 ID
     * @param reason 신고 사유 엔티티
     * @param reasonText 추가 신고 사유 텍스트(선택)
     * @return 비회원 신고용 EpisodeReport 객체
     */
    public static EpisodeReport forGuest(
        Long episodeId,
        ReportReason reason,
        String reasonText
    ) {
        return EpisodeReport.builder()
            .episodeId(episodeId)
            .reporterMemberId(null)
            .reason(reason)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    // ---------------------------------------------------
    // JPA 콜백: 저장 전 검증
    /**
     * 엔티티가 저장되기 전에 신고자가 회원인지 검증합니다.
     *
     * 신고자 회원 ID가 없으면 {@code IllegalStateException}을 발생시켜, 비회원의 신고 저장을 방지합니다.
     */

    @PrePersist
    private void validateReporter() {
        boolean hasMember = reporterMemberId != null;

        if (!hasMember) {
            throw new IllegalStateException(
                "신고는 회원만 가능합니다."
            );
        }
    }
}