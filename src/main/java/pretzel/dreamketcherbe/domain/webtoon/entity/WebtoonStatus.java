package pretzel.dreamketcherbe.domain.webtoon.entity;

public enum WebtoonStatus {
    FINISH("FINISH"),
    ;

    private String status;

    WebtoonStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
