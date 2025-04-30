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

    @Column(name = "reporter_ip", nullable = false, length = 45)
    private String reporterIp;

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
     * 회원이 신고할 때 사용.
     *
     * @param episodeId  댓글 ID
     * @param memberId   신고자 회원 ID
     * @param reason     신고 사유 엔티티
     * @param reasonText 텍스트 or null
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
            .reporterIp(null)
            .reason(reason)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    /**
     * 비회원(게스트)이 신고할 때 사용.
     *
     * @param episodeId  댓글 ID
     * @param reporterIp 신고자 IP
     * @param reason     신고 사유 엔티티
     * @param reasonText 텍스트 or null
     */
    public static EpisodeReport forGuest(
        Long episodeId,
        String reporterIp,
        ReportReason reason,
        String reasonText
    ) {
        return EpisodeReport.builder()
            .episodeId(episodeId)
            .reporterMemberId(null)
            .reporterIp(reporterIp)
            .reason(reason)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    // ---------------------------------------------------
    // JPA 콜백: 저장 전 검증
    // ---------------------------------------------------

    @PrePersist
    private void validateReporter() {
        boolean hasMember = reporterMemberId != null;
        boolean hasIp = reporterIp != null && !reporterIp.isBlank();

        if (hasMember == hasIp) {
            // 둘 다 있거나, 둘 다 없는 경우 에러
            throw new IllegalStateException(
                "신고자는 회원 또는 게스트(IP) 중 하나만 지정되어야 합니다."
            );
        }
    }
}