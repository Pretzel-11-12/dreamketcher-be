package pretzel.dreamketcherbe.domain.webtoon.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.HashTag;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.HashTagRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonHashTagRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonHashTagRepository webtoonHashTagRepository;

    private final HashTagRepository hashTagRepository;

    public List<WebtoonResDto> getWebtoonsByGenre(final String genre) {
        HashTag hashTag = hashTagRepository.findByName(genre)
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.HASH_TAG_NOT_FOUND));

        List<Long> webtoonIds = webtoonHashTagRepository.findByHashTagId(hashTag.getId())
                .stream()
                .map(webtoonHashTag -> webtoonHashTag.getWebtoon().getId())
                .collect(Collectors.toList());

        return webtoonRepository.findAllById(webtoonIds)
                .stream()
                .map(WebtoonResDto::of)
                .collect(Collectors.toList());
    }
}
