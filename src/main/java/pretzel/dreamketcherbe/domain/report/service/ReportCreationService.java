package pretzel.dreamketcherbe.domain.report.service;

import pretzel.dreamketcherbe.domain.report.dto.ReportCommand;

public interface ReportCreationService {

    Long reportComment(Long commentId, ReportCommand cmd);

    Long reportEpisode(Long webtoonId, Long episodeId, ReportCommand cmd);
}

