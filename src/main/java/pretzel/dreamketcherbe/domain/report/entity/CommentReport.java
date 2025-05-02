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
@Table(name = "comment_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CommentReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "reporter_member_id")
    private Long reporterMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = false)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "reason_text")
    private String reasonText;

    // 관리자가 처리 시
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "admin_note")
    private String adminNote;

    /**
     * 회원이 댓글을 신고할 때 새로운 CommentReport 인스턴스를 생성합니다.
     *
     * @param commentId 신고 대상 댓글의 ID
     * @param memberId 신고자 회원의 ID
     * @param reason 신고 사유 엔티티
     * @param reasonText 추가 신고 사유(선택)
     * @return 생성된 CommentReport 엔티티
     */
    public static CommentReport forMember(
        Long commentId,
        Long memberId,
        ReportReason reason,
        String reasonText
    ) {
        return CommentReport.builder()
            .commentId(commentId)
            .reporterMemberId(memberId)
            .reason(reason)
            .reasonText(reasonText)
            .status(ReportStatus.PENDING)
            .build();
    }

    /**
     * 비회원(게스트)이 댓글을 신고할 수 있도록 CommentReport 인스턴스를 생성합니다.
     *
     * @param commentId 신고 대상 댓글의 ID
     * @param reason 신고 사유 엔티티
     * @param reasonText 기타 사유 입력 시의 텍스트 또는 null
     * @return 비회원 신고용 CommentReport 객체
     */
    public static CommentReport forGuest(
        Long commentId,
        ReportReason reason,
        String reasonText
    ) {
        return CommentReport.builder()
            .commentId(commentId)
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
     * reporterMemberId가 null인 경우 예외를 발생시켜, 비회원의 신고 저장을 방지합니다.
     *
     * @throws IllegalStateException reporterMemberId가 null일 때 "신고는 회원만 가능합니다." 메시지와 함께 예외가 발생합니다.
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
