package pretzel.dreamketcherbe.domain.episode.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeLikeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeStarReqDto;
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
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Slf4j
@Service
@AllArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final WebtoonRepository webtoonRepository;
    private final MemberRepository memberRepository;
    private final WebtoonGenreRepository webtoonGenreRepository;
    private final EpisodeLikeRepository episodeLikeRepository;
    private final EpisodeStarRepository episodeStarRepository;
    private final S3Service s3Service;
    public final RedisTemplate<String, String> redisTemplate;

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

    /**
     * 에피소드 목록 조회
     */
    public WebtoonEpisodeListResDto getWebtoonEpisodes(Long webtoonId, boolean fromFirst, int page,
        int size) {

        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        List<WebtoonGenre> webtoonGenres = webtoonGenreRepository.findByWebtoonId(webtoonId);

        List<String> genreNames = webtoonGenres.stream().map(wg -> wg.getGenre().getName())
            .toList();

        PageRequest pageable = PageRequest.of(page, size);
        Page<Episode> episodePage =
            fromFirst ? episodeRepository.findByWebtoonIdOrderByPublishedAtAsc(webtoonId, pageable)
                : episodeRepository.findByWebtoonIdOrderByPublishedAtDesc(webtoonId, pageable);

        List<WebtoonEpisodeListResDto.EpisodeInfo> episodes = episodePage.getContent().stream()
            .map(this::toEpisodeInfo).toList();

        int episodeCount = (int) episodePage.getTotalElements();

        return WebtoonEpisodeListResDto.of(webtoon.getId(), webtoon.getTitle(),
            webtoon.getThumbnail(), webtoon.getStory(), episodeCount, genreNames,
            episodePage.getNumber(), episodePage.getTotalPages(), episodes);
    }

    private WebtoonEpisodeListResDto.EpisodeInfo toEpisodeInfo(Episode episode) {
        long likeCount = episodeLikeRepository.countByEpisodeId(episode.getId());
        float averageStar = calculateAverageStar(episode.getId());

        return WebtoonEpisodeListResDto.EpisodeInfo.of(episode, likeCount, averageStar);
    }

    private float calculateAverageStar(Long episodeId) {
        List<EpisodeStar> stars = episodeStarRepository.findByEpisodeId(episodeId);
        if (stars.isEmpty()) {
            return 0f;
        }
        double sum = stars.stream().mapToDouble(EpisodeStar::getPoint).sum();
        return (float) (sum / stars.size());
    }

    /**
     * 에피소드 등록
     */
    @Transactional
    public CreateEpisodeResDto createEpisode(Long memberId, CreateEpisodeReqDto request) {
        try {
            Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

            String folderName = "episode/" + memberId + "/" + request.title();

            String thumbnailUrl = s3Service.imageUpload(request.thumbnail(),
                folderName + "/thumbnail");

            List<String> contentUrl = s3Service.imagesUpload(request.content(),
                folderName + "/content");

            Episode newEpisode = Episode.builder()
                .member(findMember)
                .title(request.title())
                .thumbnail(thumbnailUrl)
                .content(contentUrl)
                .authorNote(request.authorNote())
                .build();

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
                "episode/" + memberId + "/" + webtoonId + "/" + "/thumbnail";

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
    public List<String> uploadContent(Long webtoonId, Long memberId,
        List<MultipartFile> content) {
        try {
            String folderName =
                "episode/" + memberId + "/" + webtoonId + "/" + "/content";

            return s3Service.imagesUpload(content, folderName);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
    }

    /**
     * 에피소드 컨텐츠 수정
     */
    public List<String> updateContent(List<String> existingUrls, List<MultipartFile> newImages,
        List<Integer> replaceIndices, String folderName) {
        try {
            return s3Service.updatePartialImages(existingUrls, newImages, replaceIndices,
                folderName);
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionType.IMAGE_NOT_FOUND);
        }
    }

    /**
     * 에피소드 수정
     */
    @Transactional
    public void updateEpisode(Long memberId, Long episodeId, UpdateEpisodeReqDto request) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        String folderName = "episode/" + memberId + "/" + request.title();

        String thumbnailUrl =
            request.thumbnail() != null ? s3Service.imageUpload(request.thumbnail(),
                folderName + "/thumbnail")
                : findEpisode.getThumbnail();

        List<String> contentUrls =
            request.content() != null ? s3Service.imagesUpload(request.content(),
                folderName + "/content")
                : findEpisode.getContent();

        findEpisode.isAuthor(memberId);
        findEpisode.updateTitle(request.title());
        findEpisode.updateThumbnail(thumbnailUrl);
        findEpisode.updateContent(contentUrls);
        findEpisode.updateAuthorNote(request.authorNote());
    }

    /**
     * 에피소드 삭제
     */
    @Transactional
    public void deleteEpisode(Long memberId, Long episodeId) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        findEpisode.isAuthor(memberId);

        String folderName = "episode/" + memberId + "/" + findEpisode.getTitle();

        s3Service.deleteImage(findEpisode.getThumbnail());
        for (String contentUrl : findEpisode.getContent()) {
            s3Service.deleteImage(contentUrl);
        }

        episodeRepository.delete(findEpisode);
    }

    /**
     * 에피소드 조회
     * TODO: 조회수 중복 관리 부분 리팩토링
     */
    @Transactional
    public EpisodeResDto getEpisode(Long episodeId, HttpServletRequest request,
        HttpServletResponse response) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        // 조회수 중복 방지
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewCount")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + episodeId + "]")) {
                increaseViewCount(episodeId);
                oldCookie.setValue(oldCookie.getValue() + "_" + episodeId);
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            increaseViewCount(episodeId);
            Cookie newCookie = new Cookie("viewCount", "_" + episodeId);
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }

        return EpisodeResDto.of(findEpisode);
    }

    /**
     * 조회수 증가
     */
    @Transactional
    public void increaseViewCount(Long episodeId) {
        episodeRepository.increaseViewCount(episodeId);
    }

    /**
     * 에피소드 좋아요
     */
    @Transactional
    public CreateEpisodeLikeResDto likeEpisode(Long episodeId, Long memberId) {
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
    public void starEpisode(Long memberId, Long episodeId, float point) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        EpisodeStar episodeStar = episodeStarRepository.findByMemberIdAndEpisodeId(memberId,
                episodeId)
            .orElse(null);

        EpisodeStarReqDto dto = new EpisodeStarReqDto(memberId, episodeId, point); // DTO 생성

        if (episodeStar != null) {
            episodeStar.updateOf(dto);
        } else {
            episodeStar = EpisodeStar.addOf(dto, findMember, findEpisode);
            episodeStarRepository.save(episodeStar);
        }
    }


    /**
     * 에피소드 별점 삭제
     */
    @Transactional
    public void deleteEpisodeStar(Long memberId, Long episodeId) {
        EpisodeStar episodeStar = episodeStarRepository.findByMemberIdAndEpisodeId(memberId,
                episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_STAR_NOT_FOUND));

        episodeStarRepository.delete(episodeStar);
    }
}
