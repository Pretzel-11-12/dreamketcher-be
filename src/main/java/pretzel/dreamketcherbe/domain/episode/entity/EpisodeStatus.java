package pretzel.dreamketcherbe.domain.episode.entity;

public enum EpisodeStatus {
    NORMAL,
    REPORTED,
    DELETED;

    public static EpisodeStatus of(String status) {
        return valueOf(status);
    }
}
