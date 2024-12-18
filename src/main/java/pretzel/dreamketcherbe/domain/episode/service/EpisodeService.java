package pretzel.dreamketcherbe.domain.episode.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.UpdateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.WebtoonEpisodeListResDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
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
    private final MemberRepository memberRepository;
    private final WebtoonRepository webtoonRepository;
    private final WebtoonGenreRepository webtoonGenreRepository;
    private final EpisodeLikeRepository episodeLikeRepository;
    private final EpisodeStarRepository episodeStarRepository;

    /**
     * 에피소드 목록 조회
     */
    public WebtoonEpisodeListResDto getWebtoonEpisodes(Long webtoonId, boolean fromFirst, int page,
        int size) {

        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        List<WebtoonGenre> webtoonGenres = webtoonGenreRepository.findByWebtoonId(webtoonId);

        List<String> genreNames = webtoonGenres.stream()
            .map(wg -> wg.getGenre().getName())
            .toList();

        PageRequest pageable = PageRequest.of(page, size);
        Page<Episode> episodePage = fromFirst
            ? episodeRepository.findByWebtoonIdOrderByPublishedAtAsc(webtoonId, pageable)
            : episodeRepository.findByWebtoonIdOrderByPublishedAtDesc(webtoonId, pageable);

        List<WebtoonEpisodeListResDto.EpisodeInfo> episodes = episodePage.getContent()
            .stream()
            .map(this::toEpisodeInfo)
            .toList();

        int episodeCount = (int) episodePage.getTotalElements();

        return WebtoonEpisodeListResDto.of(
            webtoon.getId(),
            webtoon.getTitle(),
            webtoon.getThumbnail(),
            webtoon.getStory(),
            episodeCount,
            genreNames,
            episodePage.getNumber(),
            episodePage.getTotalPages(),
            episodes
        );
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
    public CreateEpisodeResDto createEpisode(Long memberId, Long webtoonId,
        CreateEpisodeReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode newEpisode = Episode.builder()
            .member(findMember)
            .webtoon(findWebtoon)
            .title(request.title())
            .thumbnail(request.thumbnail())
            .content(request.content())
            .authorNote(request.authorNote())
            .build();

        episodeRepository.save(newEpisode);

        return CreateEpisodeResDto.of(newEpisode);
    }

    /**
     * 에피소드 수정
     */
    @Transactional
    public void updateEpisode(Long memberId, Long webtoonId, Long episodeId,
        UpdateEpisodeReqDto request) {

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
        findEpisode.updateTitle(request.title());
        findEpisode.updateThumbnail(request.thumbnail());
        findEpisode.updateContent(request.content());
        findEpisode.updateAuthorNote(request.authorNote());
    }

    /**
     * 에피소드 삭제
     */
    @Transactional
    public void deleteEpisode(Long memberId, Long webtoonId, Long episodeId) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        if (!findEpisode.getWebtoon().getId().equals(webtoonId)) {
            throw new EpisodeException(EpisodeExceptionType.INVALID_EPISODE);
        }

        findEpisode.isAuthor(memberId);

        episodeRepository.delete(findEpisode);
    }

    /**
     * 에피소드 조회
     * TODO: 조회수 중복 관리 부분 리팩토링
     */
    @Transactional
    public EpisodeResDto getEpisode(Long webtoonId, Long episodeId, HttpServletRequest request,
        HttpServletResponse response) {
        Webtoon findWebtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        if (!findEpisode.getWebtoon().getId().equals(webtoonId)) {
            throw new EpisodeException(EpisodeExceptionType.INVALID_EPISODE);
        }

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
}
