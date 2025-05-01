package pretzel.dreamketcherbe.domain.admin.entity;

public enum TargetType {
    COMMENT,
    EPISODE,
    USER,
    REPORT;

    public static TargetType of(String targetType) {
        return valueOf(targetType);
    }
}
