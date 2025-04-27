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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Entity
@Table(name = "comment_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CommentReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporter_ip", length = 45)
    private String reporterIp;

    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "reporter_member_id")
    private Long reporterMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = false)
    private ReportReason reasonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "reason_text")
    private String reasonText;

    // 관리자가 처리 시
    @Column(name = "processed_at")
    private DateTime processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "admin_note")
    private String adminNote;

    /**
     * 회원이 신고할 때 사용.
     *
     * @param commentId  댓글 ID
     * @param memberId   신고자 회원 ID
     * @param reasonId   신고 사유 엔티티
     * @param reasonText 텍스트 or null
     */
    public static CommentReport forMember(
        Long commentId,
        Long memberId,
        ReportReason reasonId,
        String reasonText
    ) {
        return CommentReport.builder()
            .commentId(commentId)
            .reporterMemberId(memberId)
            .reporterIp(null)
            .reasonId(reasonId)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    /**
     * 비회원(게스트)이 신고할 때 사용.
     *
     * @param commentId  댓글 ID
     * @param reporterIp 신고자 IP
     * @param reasonId   신고 사유 엔티티
     * @param reasonText ETC(기타) 텍스트 or null
     */
    public static CommentReport forGuest(
        Long commentId,
        String reporterIp,
        ReportReason reasonId,
        String reasonText
    ) {
        return CommentReport.builder()
            .commentId(commentId)
            .reporterMemberId(null)
            .reporterIp(reporterIp)
            .reasonId(reasonId)
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
