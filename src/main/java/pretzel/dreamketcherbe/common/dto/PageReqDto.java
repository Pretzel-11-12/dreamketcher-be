package pretzel.dreamketcherbe.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageReqDto {

    private final String genre;

    private final int page;

    private final int size;

    private final String order;

    @Builder
    public PageReqDto(String genre, int page, int size, String order) {
        this.genre = genre;
        this.page = page;
        this.size = size;
        this.order = order;
    }

    public static PageReqDto of(String genre, int page, int size, String order) {
        return PageReqDto.builder()
            .genre(genre)
            .page(page)
            .size(size)
            .order(order)
            .build();
    }

    public static PageReqDto of(int page, int size) {
        return PageReqDto.builder()
                .page(page)
                .size(size)
                .build();
    }

    public long getFirstIndex() {
        return (long) this.page * this.size;
    }
}
