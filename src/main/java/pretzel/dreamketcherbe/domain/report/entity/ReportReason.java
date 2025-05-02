package pretzel.dreamketcherbe.domain.report.entity;

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

@Entity
@Table(name = "report_reason")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private ReportReasonCode code;

    @Column(name = "description", length = 100, nullable = false)
    private String description;

    /**
     * 주어진 ReportReasonCode로부터 기본 설명이 포함된 ReportReason 인스턴스를 생성합니다.
     *
     * @param code ReportReasonCode 열거형 값
     * @return code와 해당 기본 설명이 설정된 ReportReason 객체
     */
    public static ReportReason fromEnum(ReportReasonCode code) {
        return ReportReason.builder()
            .code(code)
            .description(code.getDefaultDescription())
            .build();
    }
}
