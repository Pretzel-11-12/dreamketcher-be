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
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

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

    @Column(name = "webtoon_id", nullable = false)
    private Long webtoonId;

//    @Column(name = "episode_id", nullable = false)
//    private Long episodeId;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

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
     * 회원이 신고할 때 사용.
     *
     * @param memberId   신고자 회원 ID
     * @param reason     신고 사유 엔티티
     * @param reasonText 텍스트 or null
     */
    public static EpisodeReport forMember(
        Episode episode,
        Long memberId,
        ReportReason reason,
        String reasonText
    ) {
        return EpisodeReport.builder()
            .webtoonId(episode.getWebtoon().getId())
            .episode(episode)
            .reporterMemberId(memberId)
            .reason(reason)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    /**
     * 관리자가 신고를 처리할 때 사용.
     *
     * @param status      처리 상태
     * @param processedBy 처리자 회원 ID
     * @param adminNote   관리자 메모
     */
    public void reportProcess(
        ReportStatus status,
        Long processedBy,
        String adminNote,
        LocalDateTime processedAt
    ) {

        if (this.status != ReportStatus.PENDING) {
            throw new IllegalStateException(
                "신고는 대기 상태에서만 처리할 수 있습니다."
            );
        }

        if (status == null) {
            throw new IllegalStateException("처리 상태가 존재하지 않습니다.");
        }

        this.status = status;
        this.processedBy = processedBy;
        this.adminNote = adminNote;
        this.processedAt = processedAt;
    }

    // ---------------------------------------------------
    // JPA 콜백: 저장 전 검증
    // ---------------------------------------------------

    @PrePersist
    private void validateReporter() {
        boolean isMember = reporterMemberId != null;

        if (!isMember) {
            throw new IllegalStateException(
                "신고는 회원만 가능합니다."
            );
        }
    }
}