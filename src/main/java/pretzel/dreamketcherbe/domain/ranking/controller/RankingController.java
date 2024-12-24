package pretzel.dreamketcherbe.domain.ranking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.domain.ranking.dto.RankingResDto;
import pretzel.dreamketcherbe.domain.ranking.service.RankingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/webtoons/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * 랭킹 조회
     */
    @GetMapping
    public ResponseEntity<List<RankingResDto>> getRanking() {
        return ResponseEntity.ok(rankingService.getRanking());
    }
}
