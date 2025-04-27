package pretzel.dreamketcherbe.domain.report.service;

public interface ReportService {

    Long reportComment(Long commentId, ReportCommand cmd);

    Long reportEpisode(Long episodeId, ReportCommand cmd);
}

