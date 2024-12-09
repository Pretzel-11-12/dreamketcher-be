package pretzel.dreamketcherbe.domain.webtoon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonGenreRepository webtoonGenreRepository;

    private final GenreRepository GenreRepository;

    private final MemberRepository memberRepository;

    private final InterestedWebtoonRepository interestedWebtoonRepository;

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

    /*
     * 관심 웹툰 추가
     */
    @Transactional
    public void addFavoriteWebtoon(Long memberId, Long webtoonId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new WebtoonException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        if (interestedWebtoonRepository.findByMemberAndWebtoon(member, webtoon).isPresent()) {
            throw new MemberException(MemberExceptionType.ALREADY_FAVORITED);
        }

        InterestedWebtoon interestedWebtoon = InterestedWebtoon.builder()
            .member(member)
            .webtoon(webtoon)
            .build();

        interestedWebtoonRepository.save(interestedWebtoon);
    }
}
