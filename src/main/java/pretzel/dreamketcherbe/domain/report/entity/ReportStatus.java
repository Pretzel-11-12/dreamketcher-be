package pretzel.dreamketcherbe.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING,
    RESOLVED,
    DISMISSED;

    public static ReportStatus of(String status) {
        return valueOf(status);
    }
}
