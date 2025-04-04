package pretzel.dreamketcherbe.domain.webtoon.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.S3Utils.exception.S3Exception;
import pretzel.dreamketcherbe.S3Utils.exception.S3ExceptionType;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementWebtoonRespository;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.comment.repository.NotRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentNotRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeLikeRepository;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeStarRepository;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.member.service.MemberService;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.MyWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.SearchedWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonDetailResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Tag;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.TagRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonTagRepository;

@Slf4j
@Service
@AllArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final GenreRepository genreRepository;

    private final MemberRepository memberRepository;

    private final InterestedWebtoonRepository interestedWebtoonRepository;

    private final S3Service s3Service;

    private final ManagementWebtoonRespository managementWebtoonRespository;

    private final EpisodeRepository episodeRepository;

    private final CommentRepository commentRepository;

    private final RecommentRepository recommentRepository;

    private final EpisodeStarRepository episodeStarRepository;

    private final EpisodeLikeRepository episodeLikeRepository;

    private final RecommendationRepository recommendationRepository;

    private final NotRecommendationRepository notRecommendationRepository;

    private final RecommentRecommendationRepository recommentRecomendationRepository;

    private final RecommentNotRecommendationRepository recommentNotRecommendationRepository;

    private final TagRepository tagRepository;

    private final WebtoonTagRepository webtoonTagRepository;

    private final MemberService memberService;

    public final RedisTemplate<String, String> redisTemplate;

    private static final String RECOMMEND_SET_KEY_PREFIX = "comment:recommend:";
    private static final String RECOMMEND_COUNT_KEY_PREFIX = "comment:recommendCount:";
    private static final String NOT_RECOMMEND_SET_KEY_PREFIX = "comment:notRecommend:";
    private static final String NOT_RECOMMEND_COUNT_KEY_PREFIX = "comment:notRecommendCount:";

    private static final String RECOMMENT_RECOMMEND_SET_KEY_PREFIX = "recomment:recommend:";
    private static final String RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX = "recomment:recommendCount:";
    private static final String RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX = "recomment:notRecommend:";
    private static final String RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX = "recomment:notRecommendCount:";

    public static final String EPISODE_LIKE_COUNT_KEY_PREFIX = "episode:likeCount:";
    private static final String EPISODE_LIKE_USER_KEY_PREFIX = "episode:likeUser:";

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
     * 에피소드 카운트
     */
    @Transactional
    public void getEpisodeCount(Long webtoonId) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        int count = episodeRepository.CountByWebtoonId(webtoonId);
        findWebtoon.incrementEpisodeCount(count);
        webtoonRepository.save(findWebtoon);
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

        Webtoon newWebtoon = Webtoon.addOf(request, findMember, genre);
        webtoonRepository.save(newWebtoon);

        List<String> tagContents = parseTagString(request.tagsInput());
        for (String content : tagContents) {
            Tag tag = tagRepository.findByContent(content)
                .orElseGet(() -> tagRepository.save(new Tag(content)));

            newWebtoon.addTag(tag);
        }

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
    @Transactional
    public void updateWebtoon(Long memberId, Long webtoonId, UpdateWebtoonReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        findWebtoon.isAuthor(memberId);

        findWebtoon.updateOf(request);
        updateTags(findWebtoon, request.tagsInput());

        webtoonRepository.save(findWebtoon);
    }

    /**
     * 웹툰 논리 삭제
     */
    @Transactional
    public void deleteWebtoon(Long memberId, Long webtoonId) {

        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        findWebtoon.isAuthor(memberId);
        getEpisodeCount(webtoonId);
        List<Long> episodeIds = episodeRepository.findByWebtoonId(webtoonId);

        if (episodeIds.isEmpty()) {
            findWebtoon.softDelete();
            webtoonRepository.save(findWebtoon);
            memberService.deleteFavoriteWebtoon(memberId, webtoonId);
            return;
        }

        findWebtoon.softDelete();
        webtoonRepository.save(findWebtoon);
        memberService.deleteFavoriteWebtoon(memberId, webtoonId);

        episodeStarRepository.deleteByEpisode(episodeIds);
        episodeLikeRepository.deleteByEpisode(episodeIds);
        episodeRepository.deleteByWebtoonId(webtoonId);

        List<Long> commentIds = commentRepository.findByEpisodeIds(episodeIds);
        recommendationRepository.deleteByComment(commentIds);
        notRecommendationRepository.deleteByComment(commentIds);
        commentRepository.deleteByEpisodeId(episodeIds);

        List<Long> recommentIds = recommentRepository.findBycommentId(commentIds);
        recommentRecomendationRepository.deleteByRecommentId(recommentIds);
        recommentNotRecommendationRepository.deleteByRecomment(recommentIds);
        recommentRepository.deleteByCommentId(commentIds);

        deleteRedisKeys(episodeIds, commentIds, recommentIds);
    }

    /**
     * redis 삭제
     */
    private void deleteRedisKeys(List<Long> episodeIds, List<Long> commentIds,
        List<Long> recommentIds) {
        List<String> deleteKeys = new ArrayList<>();

        // 좋아요 관련 키
        for (Long episodeId : episodeIds) {
            deleteKeys.add(EPISODE_LIKE_COUNT_KEY_PREFIX + episodeId);
            deleteKeys.add(EPISODE_LIKE_USER_KEY_PREFIX + episodeId);
        }

        // 댓글 관련 키
        for (Long commentId : commentIds) {
            deleteKeys.add(RECOMMEND_SET_KEY_PREFIX + commentId);
            deleteKeys.add(RECOMMEND_COUNT_KEY_PREFIX + commentId);
            deleteKeys.add(NOT_RECOMMEND_SET_KEY_PREFIX + commentId);
            deleteKeys.add(NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId);
        }

        // 답글 관련 키
        for (Long recommentId : recommentIds) {
            deleteKeys.add(RECOMMENT_RECOMMEND_SET_KEY_PREFIX + recommentId);
            deleteKeys.add(RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId);
            deleteKeys.add(RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX + recommentId);
            deleteKeys.add(RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId);
        }

        // Redis 키 일괄 삭제
        redisTemplate.delete(deleteKeys);
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
     * 웹툰 상세 조회
     */
    public WebtoonDetailResDto getWebtoonDetail(Long webtoonId) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        return WebtoonDetailResDto.from(findWebtoon);
    }

    /**
     * 웹툰, 작가 검색
     */
    public SearchedWebtoonPageResDto searchWebtoon(
        String keyword, boolean fromFirst, int page, int size) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new WebtoonException(WebtoonExceptionType.SEARCH_KEYWORD_NOT_FOUND);
        }

        String normalizedKeyword = keyword.trim().toLowerCase();

        // fromFirst 값에 따라 정렬 기준 지정 (예시로 id 기준 오름차순/내림차순)
        Sort sort = fromFirst ? Sort.by("id").ascending() : Sort.by("id").descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<Webtoon> webtoonPage = webtoonRepository.findByTitleOrMemberNickname(normalizedKeyword,
            pageable);

        if (webtoonPage.isEmpty()) {
            return SearchedWebtoonPageResDto.of(Collections.emptyList(), page, 0, 0);
        }

        List<Long> webtoonIds = webtoonPage.getContent().stream()
            .map(Webtoon::getId)
            .collect(Collectors.toList());

        List<Object[]> starsData = episodeStarRepository.countDistinctStarsByWebtoonIds(webtoonIds);
        Map<Long, Long> webtoonIdToStars = starsData.stream()
            .collect(Collectors.toMap(
                obj -> (Long) obj[0],
                obj -> (Long) obj[1]
            ));

        List<SearchedWebtoonResDto> results = webtoonPage.getContent().stream()
            .map(webtoon -> SearchedWebtoonResDto.of(
                webtoon,
                webtoon.getGenre().getName(),
                webtoonIdToStars.getOrDefault(webtoon.getId(), 0L)
            ))
            .collect(Collectors.toList());

        return SearchedWebtoonPageResDto.of(
            results,
            webtoonPage.getNumber(),
            webtoonPage.getTotalPages(),
            (int) webtoonPage.getTotalElements()
        );
    }

    /**
     * 태그 수정
     */
    @Transactional
    public void updateTags(Webtoon webtoon, String tagsInput) {
        Map<String, Long> oldTagMap = webtoon.getWebtoonTags()
            .stream()
            .collect(Collectors.toMap(
                wt -> wt.getTag().getContent(),
                wt -> wt.getTag().getId()
            ));

        Set<String> newTagNames = new HashSet<>(parseTagString(tagsInput));

        Set<String> tagsToRemove = oldTagMap.keySet().stream()
            .filter(oldTag -> !newTagNames.contains(oldTag))
            .collect(Collectors.toSet());

        Set<String> tagsToAdd = newTagNames.stream()
            .filter(newTag -> !oldTagMap.containsKey(newTag))
            .collect(Collectors.toSet());

        for (String content : tagsToRemove) {
            Tag tag = tagRepository.findById(oldTagMap.get(content))
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.TAG_NOT_FOUND));
            webtoon.removeTag(tag);
        }

        for (String content : tagsToAdd) {
            Tag tag = tagRepository.findByContent(content)
                .orElseGet(() -> tagRepository.save(new Tag(content)));

            if (webtoon.getWebtoonTags().stream()
                .noneMatch(wt -> wt.getTag().getId().equals(tag.getId()))) {
                webtoon.addTag(tag);
            }
        }
    }

    /**
     * 해시태그 입력 파싱 "#로맨스 #액션" -> ["로맨스", "액션"]
     */
    private List<String> parseTagString(String tagsInput) {
        if (tagsInput == null || tagsInput.isEmpty()) {
            return List.of();
        }

        return List.of(tagsInput.split("\\s+"))
            .stream()
            .map(tag -> tag.replace("#", "").trim())
            .filter(tag -> !tag.isEmpty())
            .distinct()
            .collect(Collectors.toList());
    }

    //TODO: 태그 검색 Service 추가

}
