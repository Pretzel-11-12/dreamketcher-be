package pretzel.dreamketcherbe.domain.episode.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.repository.MemberRepository;

@Slf4j
@Service
@AllArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    private final MemberRepository memberRepository;

    /**
     * 에피소드 등록
     */
    public CreateEpisodeResDto createEpisode(Long memberId, CreateEpisodeReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new OptimisticLockingFailureException("존재하지 않는 회원입니다."));

        Episode newEpisode = Episode.builder()
            .member(findMember)
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
    public void updateEpisode(Long memberId, Long episodeId, CreateEpisodeReqDto request) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        findEpisode.updateTitle(request.title());
        findEpisode.updateThumbnail(request.thumbnail());
        findEpisode.updateContent(request.content());
        findEpisode.updateAuthorNote(request.authorNote());

        episodeRepository.save(findEpisode);
    }

    /**
     * 에피소드 삭제
     */
    public void deleteEpisode(Long memberId, Long EpisodeId) {
        Episode findEpisode = episodeRepository.findById(EpisodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        episodeRepository.delete(findEpisode);
    }
}
