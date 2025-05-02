package pretzel.dreamketcherbe.domain.report.dto;

public record ReportCommand(

    Long reporterMemberId,
    Long reasonId,
    String reasonText
) {

}
