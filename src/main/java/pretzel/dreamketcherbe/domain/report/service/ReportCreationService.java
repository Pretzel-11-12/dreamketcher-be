package pretzel.dreamketcherbe.domain.report.service;

import pretzel.dreamketcherbe.domain.report.dto.ReportCommand;

public interface ReportCreationService {

    /**
 * 지정된 댓글을 신고 처리하고 신고 내역의 식별자를 반환합니다.
 *
 * @param commentId 신고할 댓글의 식별자
 * @param cmd 신고에 필요한 정보가 담긴 명령 객체
 * @return 생성된 신고 내역의 식별자
 */
Long reportComment(Long commentId, ReportCommand cmd);

    /**
 * 에피소드를 신고하고 신고 내역의 식별자를 반환합니다.
 *
 * @param episodeId 신고할 에피소드의 식별자
 * @param cmd 신고에 대한 세부 정보가 담긴 명령 객체
 * @return 생성된 신고 내역의 식별자
 */
Long reportEpisode(Long episodeId, ReportCommand cmd);
}

