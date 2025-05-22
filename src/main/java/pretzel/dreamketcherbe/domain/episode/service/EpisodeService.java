package pretzel.dreamketcherbe.domain.episode.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.S3Utils.exception.S3Exception;
import pretzel.dreamketcherbe.S3Utils.exception.S3ExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.comment.repository.NotRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentNotRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeLikeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeStarReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeStarResDto;
import pretzel.dreamketcherbe.domain.episode.dto.MemberEpisodeLikeAndStarResDto;
import pretzel.dreamketcherbe.domain.episode.dto.UpdateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.WebtoonEpisodeListResDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeLike;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeStar;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeLikeRepository;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeStarRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.repository.EpisodeReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.ReportReasonRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;
import pretzel.dreamketcherbe.domain.webtoon.service.WebtoonService;

@Service
@AllArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final WebtoonRepository webtoonRepository;
    private final MemberRepository memberRepository;
    private final EpisodeLikeRepository episodeLikeRepository;
    private final EpisodeStarRepository episodeStarRepository;
    private final S3Service s3Service;
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

    private static final String LIKE_SCRIPT = """
        if redis.call('sismember', KEYS[1], ARGV[1]) == 1 then
            redis.call('srem', KEYS[1], ARGV[1])
            redis.call('decr', KEYS[2])
            return -1
        else
            redis.call('sadd', KEYS[1], ARGV[1])
            redis.call('incr', KEYS[2])
            return 1
        end
        """;

    private final RedisScript<Long> likeScript = new DefaultRedisScript<>(LIKE_SCRIPT, Long.class);
    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final NotRecommendationRepository notRecommendationRepository;
    private final RecommentNotRecommendationRepository recommentNotRecommendationRepository;
    private final WebtoonService webtoonService;
    private final ReportReasonRepository reportReasonRepository;
    private final EpisodeReportRepository episodeReportRepository;

    /**
     * 에피소드 목록 조회
     */
    public WebtoonEpisodeListResDto getWebtoonEpisodes(Long webtoonId, boolean fromFirst, int page,
        int size) {

        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        webtoonService.getEpisodeCount(webtoonId);

        PageRequest pageable = PageRequest.of(page, size);
        Page<Episode> episodePage = fromFirst
            ? episodeRepository.findByWebtoonIdOrderByPublishedAtAsc(webtoonId, pageable)
            : episodeRepository.findByWebtoonIdOrderByPublishedAtDesc(webtoonId, pageable);

        List<WebtoonEpisodeListResDto.EpisodeInfo> episodes = episodePage.getContent()
            .stream()
            .map(this::toEpisodeInfo)
            .toList();

        return WebtoonEpisodeListResDto.of(
            webtoon.getId(),
            (int) episodePage.getTotalElements(),
            episodePage.getNumber(),
            episodePage.getTotalPages(),
            episodes
        );
    }

    /**
     * 에피소드 범위 조회
     */
    public WebtoonEpisodeListResDto getWebtoonEpisodesAround(
        Long webtoonId, Long episodeId, int range) {

        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode currentEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        int currentEpisodeNo = currentEpisode.getNo();
        int totalEpisodes = Math.toIntExact(episodeRepository.countByWebtoonId(webtoonId));

        int startNo = Math.max(currentEpisodeNo - range, 1);
        int endNo = Math.min(currentEpisodeNo + range, totalEpisodes);

        List<WebtoonEpisodeListResDto.EpisodeInfo> episodes = episodeRepository
            .findEpisodesAround(webtoonId, startNo, endNo)
            .stream()
            .map(this::toEpisodeInfo)
            .toList();

        return WebtoonEpisodeListResDto.of(
            webtoon.getId(),
            totalEpisodes,
            0,
            0,
            episodes
        );
    }

    private WebtoonEpisodeListResDto.EpisodeInfo toEpisodeInfo(Episode episode) {
        long likeCount = episodeLikeRepository.countByEpisodeId(episode.getId());
        float averageStar = calculateAverageStar(episode.getId());

        return WebtoonEpisodeListResDto.EpisodeInfo.of(episode, likeCount, averageStar);
    }

    private float calculateAverageStar(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        List<EpisodeStar> stars = episodeStarRepository.findByEpisodeId(episodeId);
        if (stars.isEmpty()) {
            return 0f;
        }

        double sum = stars.stream().mapToDouble(EpisodeStar::getPoint).sum();
        float average = (float) (sum / stars.size());

        episode.updateAverageStar(average);
        episodeRepository.save(episode);

        return average;
    }

    /**
     * 에피소드 등록
     */
    @Transactional
    public CreateEpisodeResDto createEpisode(Long memberId, Long webtoonId,
        CreateEpisodeReqDto request) {
        try {
            Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

            Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

            Long episodeCount = episodeRepository.countByWebtoonId(webtoonId);
            int nextEpisodeNo = episodeCount.intValue() + 1;

            Episode newEpisode = Episode.addOf(request, nextEpisodeNo, findWebtoon, findMember,
                new ObjectMapper());

            episodeRepository.save(newEpisode);

            return CreateEpisodeResDto.of(newEpisode);
        } catch (Exception e) {
            throw new EpisodeException(EpisodeExceptionType.CREATE_EPISODE_FAILED);
        }
    }

    /**
     * 에피소드 썸네일 등록
     */
    public String uploadThumbnail(Long webtoonId, Long memberId,
        MultipartFile thumbnail) {
        try {
            String folderName =
                "episode/" + memberId + "/" + webtoonId + "thumbnail";

            return s3Service.imageUpload(thumbnail, folderName);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
    }

    /**
     * 에피소드 썸네일 수정
     */
    public String updateThumbnail(String oldThumbnail, MultipartFile newThumbnail,
        String folderName) {
        try {
            return s3Service.imageUpdate(oldThumbnail, newThumbnail, folderName);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.IMAGE_NOT_FOUND);
        }
    }

    /**
     * 에피소드 컨텐츠 등록
     */
    public String uploadContent(Long webtoonId, Long memberId,
        List<MultipartFile> content, ObjectMapper objectMapper) {
        try {
            String folderName =
                "episode/" + memberId + webtoonId + "content";

            List<String> contentUrls = s3Service.imagesUpload(content, folderName);

            return objectMapper.writeValueAsString(contentUrls);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
    }

    /**
     * 에피소드 컨텐츠 수정
     */
    public String updateContent(String existingUrlsJson, List<MultipartFile> newImages,
        List<Integer> replaceIndices, String folderName, ObjectMapper objectMapper) {
        try {
            List<String> oldContentUrls = objectMapper.readValue(existingUrlsJson,
                new TypeReference<>() {
                });

            List<String> updateContentUrls = s3Service.updatePartialImages(oldContentUrls,
                newImages, replaceIndices,
                folderName);

            return objectMapper.writeValueAsString(updateContentUrls);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.IMAGE_NOT_FOUND);
        }
    }

    /**
     * 에피소드 이미지 삭제
     */
    public void deleteImage(String imageUrl) {
        try {
            s3Service.deleteImage(imageUrl);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.DELETE_FAILED);
        }
    }

    /**
     * 에피소드 수정
     */
    @Transactional
    public void updateEpisode(Long memberId, Long webtoonId, Long episodeId,
        UpdateEpisodeReqDto request) throws JsonProcessingException {

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        if (!findEpisode.getWebtoon().getId().equals(webtoonId)) {
            throw new EpisodeException(EpisodeExceptionType.INVALID_EPISODE);
        }

        // 작성자 검증
        findEpisode.isAuthor(memberId);

        // 에피소드 수정
        findEpisode.updateOf(request, new ObjectMapper());
        episodeRepository.save(findEpisode);
    }

    /**
     * 에피소드 논리 삭제
     */
    @Transactional
    public void deleteEpisode(Long memberId, Long webtoonId, Long episodeId) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        findEpisode.isAuthor(memberId);
        List<Long> commentIds = commentRepository.findByEpisodeId(episodeId);
        List<Long> recommentIds = recommentRepository.findBycommentId(commentIds);

        if (commentIds.isEmpty()) {
            episodeStarRepository.deleteByEpisodeId(episodeId);
            episodeLikeRepository.deleteByEpisodeId(episodeId);
            findEpisode.softDelete();
            episodeRepository.save(findEpisode);
            return;
        }

        episodeStarRepository.deleteByEpisodeId(episodeId);
        episodeLikeRepository.deleteByEpisodeId(episodeId);
        findEpisode.softDelete();
        episodeRepository.save(findEpisode);

        recommentRepository.deleteByCommentId(commentIds);
        notRecommendationRepository.deleteByComment(commentIds);
        commentRepository.deleteByEpisode(episodeId);

        recommentNotRecommendationRepository.deleteByRecomment(recommentIds);
        recommentNotRecommendationRepository.deleteByRecomment(recommentIds);
        recommentRepository.deleteByCommentId(commentIds);

        deleteRedisKeys(commentIds, recommentIds);
    }

    /**
     * redis 추천/비추천 삭제
     */
    private void deleteRedisKeys(List<Long> commentIds, List<Long> recommentIds) {
        List<String> deleteKeys = new ArrayList<>();

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
     * 에피소드 조회
     */
    @Transactional
    public EpisodeResDto getEpisode(Long webtoonId, Long episodeId, HttpServletRequest request,
        HttpServletResponse response) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode findEpisode = episodeRepository.findByIsDeletedFalseAndPublishedTrue(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        if (!findEpisode.getWebtoon().getId().equals(webtoonId)) {
            throw new EpisodeException(EpisodeExceptionType.INVALID_EPISODE);
        }

        addViewCount(request, episodeId);

        calculateAverageStar(episodeId);

        return EpisodeResDto.of(findEpisode);
    }

    @Transactional
    public Cookie addViewCount(HttpServletRequest request, Long episodeId) {

        Cookie[] cookies = request.getCookies();
        Cookie oldCookie = this.getCookie(cookies, "view_count");

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + episodeId + "]")) {
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                increaseViewCount(episodeId);
            }
            return oldCookie;
        } else {
            Cookie newCookie = new Cookie("view_count", "[" + episodeId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            increaseViewCount(episodeId);
            return newCookie;
        }
    }

    private Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 조회수 증가
     */
    public void increaseViewCount(Long episodeId) {
        episodeRepository.increaseViewCount(episodeId);
    }

    /**
     * 사용자 에피소드 좋아요, 별점 조회
     */
    public MemberEpisodeLikeAndStarResDto getMemberEpisodeLikeAndStar(Long memberId,
        Long episodeId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Optional<EpisodeLike> episodeLike = episodeLikeRepository.findByEpisodeAndMember(
            episodeId, memberId);

        Optional<EpisodeStar> episodeStar = episodeStarRepository.findByMemberIdAndEpisodeId(
            memberId, episodeId);

        return MemberEpisodeLikeAndStarResDto.of(episodeStar.orElse(null),
            episodeLike.orElse(null));
    }

    /**
     * 에피소드 좋아요
     */
    @Transactional
    public CreateEpisodeLikeResDto likeEpisode(Long webtoonId, Long episodeId, Long memberId) {
        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        String likeCountKey = EPISODE_LIKE_COUNT_KEY_PREFIX + episodeId;
        String likeUserKey = EPISODE_LIKE_USER_KEY_PREFIX + episodeId;

        Long result = redisTemplate.execute(likeScript, List.of(likeUserKey, likeCountKey),
            memberId.toString());

        if (result == null) {
            throw new IllegalStateException("Redis execution failed");
        }

        if (result == 1) {
            episodeLikeRepository.save(new EpisodeLike(episode, member));
        } else if (result == -1) {
            episodeLikeRepository.deleteByEpisodeAndMember(episodeId, memberId);
        }

        String likeCount = redisTemplate.opsForValue().get(likeCountKey);
        int likeCountInt = likeCount == null ? 0 : Integer.parseInt(likeCount);

        return CreateEpisodeLikeResDto.of(episodeId, likeCountInt);
    }

    /**
     * 에피소드 좋아요 수 동기화
     */
    @Transactional
    public void syncEpisodeLikeCount(Long episodeId) {
        String likeCountkey = EPISODE_LIKE_COUNT_KEY_PREFIX + episodeId;

        String likeCount = redisTemplate.opsForValue().get(likeCountkey);
        if (likeCount != null) {
            int likeCountInt = Integer.parseInt(likeCount);
            Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

            episode.setLikeCount(likeCountInt);
            episodeRepository.save(episode);
        }
    }

    /**
     * Redis 장애 대비
     */
    @Transactional
    public int getLikeCountFallback(Long episodId) {
        long likeCount = episodeLikeRepository.countByEpisodeId(episodId);
        Episode episode = episodeRepository.findById(episodId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        episode.setLikeCount((int) likeCount);
        episodeRepository.save(episode);
        return (int) likeCount;
    }

    /**
     * Redis 데이터 초기화 및 재동기화
     */
    @Transactional
    public void initializeRedisLikeCount(Long episodeId) {
        String likeCountKey = EPISODE_LIKE_COUNT_KEY_PREFIX + episodeId;
        String likeUserKey = EPISODE_LIKE_USER_KEY_PREFIX + episodeId;

        int likeCount = getLikeCountFallback(episodeId);
        redisTemplate.opsForValue().set(likeCountKey, String.valueOf(likeCount), 1, TimeUnit.DAYS);

        List<EpisodeLike> likes = episodeLikeRepository.findAllById(episodeId);
        for (EpisodeLike like : likes) {
            redisTemplate.opsForSet().add(likeUserKey, like.getMember().getId().toString());
        }

        redisTemplate.expire(likeUserKey, 1, TimeUnit.DAYS);
        redisTemplate.expire(likeCountKey, 1, TimeUnit.DAYS);
    }

    /**
     * 에피소드 별점
     */
    @Transactional
    public EpisodeStarResDto starEpisode(Long memberId, Long webtoonId, Long episodeId,
        float point) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        EpisodeStar episodeStar = episodeStarRepository.findByMemberIdAndEpisodeId(memberId,
                episodeId)
            .orElse(null);

        EpisodeStarReqDto dto = new EpisodeStarReqDto(memberId, webtoonId, episodeId,
            point); // DTO 생성

        if (episodeStar != null) {
            episodeStar.updateOf(dto);

            return EpisodeStarResDto.of(episodeStar.getId(), episodeStar.getPoint());
        } else {
            episodeStar = EpisodeStar.addOf(dto, findMember, findEpisode);
            episodeStarRepository.save(episodeStar);

            return EpisodeStarResDto.of(episodeStar.getId(), episodeStar.getPoint());
        }
    }


    /**
     * 에피소드 별점 삭제
     */
    @Transactional
    public void deleteEpisodeStar(Long memberId, Long webtoonId, Long episodeId) {
        EpisodeStar episodeStar = episodeStarRepository.findByMemberIdAndEpisodeId(memberId,
                episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_STAR_NOT_FOUND));

        episodeStarRepository.delete(episodeStar);
    }

    /**
     * 에피소드 신고
     */
    @Transactional
    public void reportEpisode(Long memberId, Long webtoonId, Long episodeId, Long reasonId,
        String reasonText) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        if (!findEpisode.getWebtoon().getId().equals(findWebtoon.getId())) {
            throw new EpisodeException(EpisodeExceptionType.INVALID_EPISODE);
        }

        ReportReason findReason = reportReasonRepository.findById(reasonId)
            .orElseThrow(() -> new IllegalStateException()); // 추후 수정

        EpisodeReport findEpisodeReport = EpisodeReport.forMember(webtoonId, episodeId, memberId,
            findReason,
            reasonText);

        if (episodeReportRepository.existsByEpisodeIdAndMemberId(episodeId, memberId)) {
            throw new EpisodeException(EpisodeExceptionType.REPORTED_EPISODE);
        }
        episodeReportRepository.save(findEpisodeReport);

        findEpisode.report();
        episodeRepository.save(findEpisode);
    }

    /**
     * 에피소드 신고 해제
     */
    @Transactional
    public void normalizeEpiosde(Long memberId, Long webtoonId, Long episodeId) {
        Episode episode = episodeRepository.findReportedEpisodeById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        if (episodeReportRepository.findResolvedEpisodeReportByEpisodeId(episodeId) != null) {
            episode.normalize();
        }
        episodeRepository.save(episode);
    }
}
