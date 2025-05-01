package pretzel.dreamketcherbe.domain.report.service;

import pretzel.dreamketcherbe.domain.report.dto.ReportCommand;

public interface ReportCreationService {

    Long reportComment(Long commentId, ReportCommand cmd);

    Long reportEpisode(Long episodeId, ReportCommand cmd);
}

