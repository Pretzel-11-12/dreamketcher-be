package pretzel.dreamketcherbe.domain.episode.entity;

import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;

import java.util.Arrays;

public enum EpisodeStatus {
    NOT_APPROVAL("NOT_APPROVAL", "승인 전"),
    APPROVAL("APPROVAL", "승인"),
    COMPANION("COMPANION", "반려")
    ;

    private String status;
    private String value;

    EpisodeStatus(String status, String value) {
        this.status = status;
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidStatus(String status) {
        return Arrays.stream(WebtoonStatus.values())
                .anyMatch(webtoonStatus -> webtoonStatus.getStatus().equals(status));
    }
}
