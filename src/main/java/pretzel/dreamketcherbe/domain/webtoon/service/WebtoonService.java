package pretzel.dreamketcherbe.domain.webtoon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;
import java.util.stream.Collectors;
import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.repository.MemberRepository;

@Slf4j
@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonGenreRepository webtoonGenreRepository;

    private final GenreRepository GenreRepository;

    private final MemberRepository memberRepository;

    /**
     * 웹툰 장르별 목록 조회
     */
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

    /**
     * 웹툰 완결 목록 조회
     */
    public List<WebtoonResDto> getWebtoonsByFinish() {
        return webtoonRepository.findAllByStatus(WebtoonStatus.FINISH.getStatus())
            .stream()
            .map(WebtoonResDto::of)
            .collect(Collectors.toList());
    }

    /*
     * 웹툰 등록
     * Todo : 예외 처리, 추후 MemberExceptionType 생성시 수정
     */
    public CreateWebtoonResDto createWebtoon(Long memberId, CreateWebtoonReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new OptimisticLockingFailureException("회원을 찾을 수 없습니다."));

        Webtoon newWebtoon = Webtoon.builder()
            .title(request.title())
            .thumbnail(request.thumbnail())
            .prologue(request.prologue())
            .story(request.story())
            .description(request.description())
            .approval("not_approval")
            .status("pre_series")
            .member(findMember)
            .build();

        webtoonRepository.save(newWebtoon);

        return CreateWebtoonResDto.of(newWebtoon);
    }
}
