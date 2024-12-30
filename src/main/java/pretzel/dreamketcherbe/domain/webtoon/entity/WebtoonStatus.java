package pretzel.dreamketcherbe.domain.webtoon.entity;

import java.util.Arrays;

public enum WebtoonStatus {
    FINISH("FINISH", "완결"),
    IN_SERIES("IN_SERIES", "연재중"),
    REST("REST", "휴재");

    private String status;
    private String value;

    WebtoonStatus(String status, String value) {
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
