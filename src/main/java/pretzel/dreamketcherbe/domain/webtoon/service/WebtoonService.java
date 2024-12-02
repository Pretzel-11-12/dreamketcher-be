package pretzel.dreamketcherbe.domain.webtoon.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonGenreRepository webtoonGenreRepository;

    private final GenreRepository GenreRepository;

    public List<WebtoonResDto> getWebtoonsByGenre(final String genreName) {
        Genre genre = GenreRepository.findByName(genreName)
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND));

        List<Long> webtoonIds = webtoonGenreRepository.findByGenreId(genre.getId())
                .stream()
                .map(webtoonGenre -> webtoonGenre.getWebtoon().getId())
                .collect(Collectors.toList());

        return webtoonRepository.findAllById(webtoonIds)
                .stream()
                .map(WebtoonResDto::of)
                .collect(Collectors.toList());
    }
}
