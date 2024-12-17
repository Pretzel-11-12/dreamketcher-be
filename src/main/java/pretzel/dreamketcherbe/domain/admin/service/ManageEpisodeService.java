package pretzel.dreamketcherbe.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.admin.dto.ManageEpisodeResDto;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementEpisode;
import pretzel.dreamketcherbe.domain.admin.exception.AdminException;
import pretzel.dreamketcherbe.domain.admin.exception.AdminExceptionType;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementEpisodeRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;

@Service
@RequiredArgsConstructor
public class ManageEpisodeService {

    private final EpisodeRepository episodeRepository;

    private final ManagementEpisodeRepository managementEpisodeRepository;

    /**
     * 에피소드 목록 조회
     */
    public Page<ManageEpisodeResDto> getEpisodes(Long webtoonId, Pageable pageable) {
        Page<Episode> episodes = episodeRepository.findAllByWebtoonId(webtoonId, pageable);

        return episodes.map(episode -> {
            ManagementEpisode managementEpisode = managementEpisodeRepository.findByEpisodeId(episode.getId())
                    .orElseThrow(() -> new AdminException(AdminExceptionType.MANAGE_EPISODE_NOT_FOUND));

            return ManageEpisodeResDto.of(episode, managementEpisode);
        });
    }
}
