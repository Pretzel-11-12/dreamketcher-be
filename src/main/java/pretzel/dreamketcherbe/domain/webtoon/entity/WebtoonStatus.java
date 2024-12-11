package pretzel.dreamketcherbe.domain.webtoon.entity;

public enum WebtoonStatus {
    FINISH("FINISH"),
    IN_SERIES("IN_SERIES")
    ;

    private String status;

    WebtoonStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
