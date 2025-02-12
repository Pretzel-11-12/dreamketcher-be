package pretzel.dreamketcherbe.domain.webtoon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.S3Utils.exception.S3Exception;
import pretzel.dreamketcherbe.S3Utils.exception.S3ExceptionType;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementWebtoonRespository;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeStarRepository;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.webtoon.dto.*;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final GenreRepository genreRepository;

    private final MemberRepository memberRepository;

    private final InterestedWebtoonRepository interestedWebtoonRepository;

    private final S3Service s3Service;

    private final ManagementWebtoonRespository managementWebtoonRespository;

    private final EpisodeStarRepository episodeStarRepository;

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

    /**
     * 웹툰 등록
     */
    @Transactional
    public CreateWebtoonResDto createWebtoon(Long memberId, CreateWebtoonReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Genre genre = genreRepository.findById(request.genreId())
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND));

        Webtoon newWebtoon = Webtoon.addOf(request, findMember, genre, new ObjectMapper());
        webtoonRepository.save(newWebtoon);

        ManagementWebtoon managementWebtoon = ManagementWebtoon.addOf(newWebtoon);
        managementWebtoonRespository.save(managementWebtoon);

        return CreateWebtoonResDto.of(newWebtoon);
    }

    /**
     * 웹툰 썸네일 등록
     */
    public String uploadThumbnail(Long memberId, MultipartFile thumbnail) {
        try {
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

            String folderName = "webtoon/" + memberId + "/thumbnail";

            return s3Service.imageUpload(thumbnail, folderName);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
    }

    /**
     * 웹툰 썸네일 수정
     */
    public String updateThumbnail(String oldThumbnail, MultipartFile newThumbnail,
        String folderName) {
        try {
            return s3Service.imageUpdate(oldThumbnail, newThumbnail, folderName);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPDATE_FAILED);
        }
    }

    /**
     * 웹툰 썸네일 삭제
     */
    public void deleteThumbnail(String imageUrl) {
        try {
            s3Service.deleteImage(imageUrl);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.DELETE_FAILED);
        }
    }

    /**
     * 웹툰 프롤로그 등록
     */
    public String uploadPrologue(Long memberId, List<MultipartFile> prologue,
        ObjectMapper objectMapper) {
        try {
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

            String folderName = "webtoon/" + memberId + "/prologue";

            List<String> prologueImageUrls = s3Service.imagesUpload(prologue, folderName);

            return objectMapper.writeValueAsString(prologueImageUrls);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
    }

    /**
     * 웹툰 프롤로그 수정
     */
    public String updatePrologue(String oldPrologueJson, List<MultipartFile> newPrologue,
        List<Integer> replaceIndex, String folderName, ObjectMapper objectMapper) {
        try {

            List<String> oldPrologue = objectMapper.readValue(oldPrologueJson,
                new TypeReference<>() {
                });

            List<String> updatedPrologueUrls = s3Service.updatePartialImages(oldPrologue,
                newPrologue, replaceIndex, folderName);

            return objectMapper.writeValueAsString(updatedPrologueUrls);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPDATE_FAILED);
        }
    }

    /**
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

        webtoon.incrementInterestCount(1);
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

        findWebtoon.updateOf(request, new ObjectMapper());

        webtoonRepository.save(findWebtoon);
    }

    /**
     * 웹툰 삭제
     */
    @Transactional
    public void deleteWebtoon(Long memberId, Long webtoonId) {

        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        findWebtoon.isAuthor(memberId);

        findWebtoon.softDelete();

        webtoonRepository.save(findWebtoon);
    }

    /**
     * 내 작품 조회
     */
    public MyWebtoonResDto getMyWebtoon(Long MemberId, Long webtoonId) {
        Member findMember = memberRepository.findById(MemberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        if (!findWebtoon.getMember().getId().equals(findMember.getId())) {
            throw new WebtoonException(WebtoonExceptionType.NO_AUTHORITY_WEBTOON);
        }

        return MyWebtoonResDto.of(findWebtoon, findWebtoon.getGenre().getName());
    }

    /**
     * 웹툰, 작가 검색
     */
    public List<SearchedWebtoonResDto> searchWebtoon(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new WebtoonException(WebtoonExceptionType.SEARCH_KEYWORD_NOT_FOUND);
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        List<Webtoon> webtoons = webtoonRepository.findByTitleOrMemberNickname(normalizedKeyword);

        if (webtoons.isEmpty()) {
            return List.of();
        }

        List<Long> webtoonIds = webtoons.stream()
            .map(Webtoon::getId)
            .collect(Collectors.toList());

        List<Object[]> starsData = episodeStarRepository.countDistinctStarsByWebtoonIds(webtoonIds);
        Map<Long, Long> webtoonIdToStars = starsData.stream()
            .collect(Collectors.toMap(
                obj -> (Long) obj[0],
                obj -> (Long) obj[1]
            ));

        return webtoons.stream()
            .map(webtoon -> SearchedWebtoonResDto.of(
                webtoon,
                webtoon.getGenre().getName(),
                webtoonIdToStars.getOrDefault(webtoon.getId(), 0L)
            ))
            .collect(Collectors.toList());
    }
}
