package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;

public record SearchedWebtoonPageResDto(
    List<SearchedWebtoonResDto> results,
    int currentPage,
    int totalPages,
    int totalElements
) {

    public static SearchedWebtoonPageResDto of(List<SearchedWebtoonResDto> results, int currentPage,
        int totalPages, int totalElements) {
        return new SearchedWebtoonPageResDto(results, currentPage, totalPages, totalElements);
    }
}