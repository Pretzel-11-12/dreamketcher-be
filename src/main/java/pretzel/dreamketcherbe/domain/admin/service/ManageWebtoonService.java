package pretzel.dreamketcherbe.domain.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.admin.dto.ManageWebtoonResDto;
import pretzel.dreamketcherbe.domain.admin.dto.UpdateWebtoonStatusReqDto;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.exception.AdminException;
import pretzel.dreamketcherbe.domain.admin.exception.AdminExceptionType;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementWebtoonRespository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Service
@AllArgsConstructor
public class ManageWebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonGenreRepository webtoonGenreRepository;

    private final GenreRepository genreRepository;

    private final ManagementWebtoonRespository managementWebtoonRespository;

    /**
     * 웹툰 목록 조회
     */
    public Page<ManageWebtoonResDto> getWebtoons(Pageable pageable) {
        Page<Webtoon> webtoons = webtoonRepository.findAllByOrderByCreatedAtDesc(pageable);

        return webtoons.map(webtoon -> {
            WebtoonGenre webtoonGenre = webtoonGenreRepository.findByWebtoonId(webtoon.getId())
                .orElseThrow(
                    () -> new WebtoonException(WebtoonExceptionType.WEBTOON_GENRE_NOT_FOUND));

            Genre genre = genreRepository.findById(webtoonGenre.getGenre().getId())
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND));

            ManagementWebtoon manangeWebtoon = managementWebtoonRespository.findByWebtoonId(
                    webtoon.getId())
                .orElseThrow(() -> new AdminException(AdminExceptionType.MANAGE_WEBTOON_NOT_FOUND));

            return ManageWebtoonResDto.of(webtoon, genre, manangeWebtoon);
        });
    }

    /**
     * 작품 상태 변경
     */
    @Transactional
    public void updateWebtoonStatus(UpdateWebtoonStatusReqDto updateWebtoonStatusReqDto) {
        if (!WebtoonStatus.isValidStatus(updateWebtoonStatusReqDto.status())) {
            throw new WebtoonException(WebtoonExceptionType.WEBTOON_STATUS_NOT_FOUND);
        }

        for (Long id : updateWebtoonStatusReqDto.webtoonIds()) {
            Webtoon webtoon = webtoonRepository.findById(id)
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));
            webtoon.updateStatus(updateWebtoonStatusReqDto.status());
            webtoonRepository.save(webtoon);
        }
    }
}
