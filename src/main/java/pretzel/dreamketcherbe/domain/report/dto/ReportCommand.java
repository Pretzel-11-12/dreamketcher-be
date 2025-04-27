package pretzel.dreamketcherbe.domain.report.dto;

public record ReportCommand(

    Long reporterMemberId,
    String reporterIp,
    Long reasonId,
    String reasonText
) {

}
