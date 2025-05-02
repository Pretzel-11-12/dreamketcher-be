package pretzel.dreamketcherbe.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING,
    RESOLVED,
    DISMISSED;

    /**
     * 주어진 문자열에 해당하는 ReportStatus 열거형 상수를 반환합니다.
     *
     * @param status 변환할 상태 문자열
     * @return 해당하는 ReportStatus 열거형 상수
     * @throws IllegalArgumentException 유효하지 않은 상태 문자열이 전달된 경우 발생합니다.
     */
    public static ReportStatus of(String status) {
        return valueOf(status);
    }
}
