package pretzel.dreamketcherbe.domain.comment.entity;

public enum CommentStatus {
    NORMAL,
    REPORTED,
    DELETED;

    public static CommentStatus of(String status) {
        return valueOf(status);
    }
}