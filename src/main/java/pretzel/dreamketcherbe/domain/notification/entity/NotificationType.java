package pretzel.dreamketcherbe.domain.notification.entity;

public enum NotificationType {
    REPORT_SUBMITTED("신고가 제출되었습니다."),
    REPORT_PROCESSED("신고가 처리 중입니다."),
    REPORT_RECEIVED("에피소드가 신고 접수 되었습니다."),
    REPORT_REJECTED("신고가 반려되었습니다."),
    REPORT_APPROVED("신고가 승인되었습니다.");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}


