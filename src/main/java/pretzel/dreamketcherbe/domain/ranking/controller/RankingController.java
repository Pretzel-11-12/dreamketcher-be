package pretzel.dreamketcherbe.domain.ranking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * 랭킹 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<RankingResDto>> getAllRanking(@RequestParam(defaultValue = "none") String genre,
                                                             @RequestParam(defaultValue = "none") String status) {
        return ResponseEntity.ok(rankingService.getRanking(genre, status));
    }

//    /**
//     * 신작 랭킹 목록 조회
//     */
//    @GetMapping("/new")
//    public ResponseEntity<List<RankingResDto>> getNewRanking(@RequestParam(defaultValue = "none") String genre) {
//        return ResponseEntity.ok(rankingService.getNewRanking(genre));
//    }
//
//    /**
//     * 완결 랭킹 목록 조회
//     */
//    @GetMapping("/finish")
//    public ResponseEntity<List<RankingResDto>> getFinishRanking(@RequestParam(defaultValue = "none") String genre) {
//        return ResponseEntity.ok(rankingService.getFinishRanking(genre));
//    }
}
