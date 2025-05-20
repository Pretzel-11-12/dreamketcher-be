package pretzel.dreamketcherbe.domain.comment.entity;

public enum RecommentStatus {
    NORMAL,
    REPORTED,
    DELETED;

    public static RecommentStatus of(String status) {
        return valueOf(status);
    }
}
