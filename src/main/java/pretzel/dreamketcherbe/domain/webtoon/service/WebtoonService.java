package pretzel.dreamketcherbe.domain.webtoon.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementWebtoonRespository;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.webtoon.dto.*;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final WebtoonGenreRepository webtoonGenreRepository;

    private final GenreRepository genreRepository;

    private final MemberRepository memberRepository;

    private final InterestedWebtoonRepository interestedWebtoonRepository;
    private final S3Service s3Service;

    private final ManagementWebtoonRespository managementWebtoonRespository;

    /**
     * 연재중인 웹툰 목록 조회
     */
    public PageResDto<WebtoonResDto> getWebtoons(PageReqDto pageReqDto) {
        if (!pageReqDto.getGenre().equals("none")) {
            genreRepository.findByName(pageReqDto.getGenre())
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND));
        }

        return webtoonRepository.findWebtoonsWithPage(WebtoonStatus.IN_SERIES.getStatus(),
            pageReqDto);
    }

    /**
     * 웹툰 완결 전체 목록 조회
     */
    public PageResDto<WebtoonResDto> getWebtoonsByFinish(PageReqDto pageReqDto) {
        if (!pageReqDto.getGenre().equals("none")) {
            genreRepository.findByName(pageReqDto.getGenre())
                    .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND));
        }

        return webtoonRepository.findWebtoonsWithPage(WebtoonStatus.FINISH.getStatus(), pageReqDto);
    }

    /**
     * 웹툰 신작 전체 목록 조회
     */
    public PageResDto<WebtoonResDto> getWebtoonsByNew(PageReqDto pageReqDto) {
        if (!pageReqDto.getGenre().equals("none")) {
            genreRepository.findByName(pageReqDto.getGenre())
                    .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND));
        }
        
        return webtoonRepository.findWebtoonsWithPage(WebtoonStatus.NEW.getStatus(), pageReqDto);
    }

    /*
     * 웹툰 등록
     */
    @Transactional
    public CreateWebtoonResDto createWebtoon(Long memberId, CreateWebtoonReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        String folderName = "/webtoon" + findMember.getId() + "/" + request.title();

        String thumbnailUrl = s3Service.imageUpload(request.thumbnail(), folderName + "/thumbnail");
        List<String> prologueUrl = s3Service.imagesUpload(request.prologue(),
            folderName + "/prologue");

        Webtoon newWebtoon = Webtoon.addOf(request, findMember, thumbnailUrl, prologueUrl);
        webtoonRepository.save(newWebtoon);

        ManagementWebtoon managementWebtoon = ManagementWebtoon.addOf(newWebtoon);
        managementWebtoonRespository.save(managementWebtoon);

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
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        String folderName = "/webtoon" + findMember.getId() + "/" + request.title();

        String thumbnailUrl =
            request.thumbnail() != null ? s3Service.imageUpload(request.thumbnail(),
                folderName + "/thumbnail")
                : findWebtoon.getThumbnail();

        List<String> prologueUrl =
            request.prologue() != null ? s3Service.imagesUpload(request.prologue(),
                folderName + "/prologue")
                : findWebtoon.getPrologue();

        findWebtoon.updateOf(request, thumbnailUrl, prologueUrl);

        webtoonRepository.save(findWebtoon);
    }

    /**
     * 웹툰 삭제
     */
    public void deleteWebtoon(Long memberId, Long webtoonId) {

        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

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

    private List<Long> getWebtoonIdsInGenre(String genre) {
        Long genreId = genreRepository.findByName(genre)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND))
            .getId();

        return webtoonGenreRepository.findAllByGenreId(genreId).stream()
            .map(webtoonGenre -> webtoonGenre.getWebtoon().getId())
            .toList();
    }
}
