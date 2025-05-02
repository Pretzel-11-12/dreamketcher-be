package pretzel.dreamketcherbe.domain.admin.entity;

public enum ActionType {
    COMMENT_HIDDEN,
    EPISODE_HIDDEN,
    USER_SUSPENDED,
    REPORT_DISMISSED,
    REPORT_RESOLVED,
    ;

    public static ActionType of(String actionType) {
        return valueOf(actionType);
    }
}
