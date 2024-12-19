package pretzel.dreamketcherbe.domain.webtoon.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.SearchedWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonGenreRepository webtoonGenreRepository;

    private final GenreRepository GenreRepository;

    private final MemberRepository memberRepository;

    private final InterestedWebtoonRepository interestedWebtoonRepository;
    private final S3Service s3Service;

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

    /**
     * 웹툰 신작 목록 조회
     */
    public List<WebtoonResDto> getWebtoonsByNew() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(1);

        return webtoonRepository.findAllByStatusAndCreatedAtAfter(
                WebtoonStatus.IN_SERIES.getStatus(), cutoffDate)
            .stream()
            .map(WebtoonResDto::of)
            .collect(Collectors.toList());
    }

    /*
     * 웹툰 등록
     */
    public CreateWebtoonResDto createWebtoon(Long memberId, CreateWebtoonReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        String thumbnailUrl = s3Service.imageUpload(request.thumbnail());
        List<String> prologueUrl = s3Service.imagesUpload(request.prologue());

        Webtoon newWebtoon = Webtoon.builder()
            .title(request.title())
            .thumbnail(thumbnailUrl)
            .prologue(prologueUrl)
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

    /**
     * 웹툰 수정
     */
    public void updateWebtoon(Long memberId, Long webtoonId, UpdateWebtoonReqDto request) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        String thumbnailUrl =
            request.thumbnail() != null ? s3Service.imageUpload(request.thumbnail())
                : findWebtoon.getThumbnail();

        List<String> prologueUrl =
            request.prologue() != null ? s3Service.imagesUpload(request.prologue())
                : findWebtoon.getPrologue();

        findWebtoon.getId();
        findWebtoon.updateTitle(request.title());
        findWebtoon.updateThumbnail(thumbnailUrl);
        findWebtoon.updatePrologue(prologueUrl);
        findWebtoon.updateStory(request.story());
        findWebtoon.updateDescription(request.description());

        webtoonRepository.save(findWebtoon);
    }

    /**
     * 웹툰 삭제
     */
    public void deleteWebtoon(Long memberId, Long webtoonId) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        webtoonRepository.delete(findWebtoon);
    }

    /**
     * 웹툰, 작가 검색
     */
    public List<SearchedWebtoonResDto> searchWebtoon(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new WebtoonException(WebtoonExceptionType.SEARCH_KEYWORD_NOT_FOUND);
        }
        String normalizedKeyword = keyword.trim().toLowerCase();

        List<Webtoon> byTitle = webtoonRepository.findByTitleContaining(normalizedKeyword);
        List<Webtoon> byMemberNickname = webtoonRepository.findByMemberNickname(normalizedKeyword);

        return Stream.concat(byTitle.stream(), byMemberNickname.stream())
            .distinct()
            .map(SearchedWebtoonResDto::of)
            .collect(Collectors.toList());
    }
}
