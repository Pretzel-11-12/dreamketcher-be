package pretzel.dreamketcherbe.domain.report.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportCommand {

    private final Long reporterMemberId;
    private final String reporterIp;
    private final Long reasonId;
    private final String reasonText;
}
