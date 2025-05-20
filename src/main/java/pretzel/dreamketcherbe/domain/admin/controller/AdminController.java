package pretzel.dreamketcherbe.domain.admin.controller;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Admin;
import pretzel.dreamketcherbe.domain.admin.dto.AdminLogResDto;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;
import pretzel.dreamketcherbe.domain.admin.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리 로그를 필터 및 페이징 조건에 따라 조회하여 반환합니다.
     *
     * @param targetType   로그의 대상 유형(선택)
     * @param actionType   로그의 액션 유형(선택)
     * @param from         조회 시작일(yyyy.MM.dd, 선택)
     * @param to           조회 종료일(yyyy.MM.dd, 선택)
     * @param page         페이지 번호(기본값 0)
     * @param size         페이지 크기(기본값 20)
     * @return 필터 및 페이징이 적용된 관리 로그 목록
     */
    @GetMapping("/logs")
    public ResponseEntity<AdminLogResDto> getLogs(
        @Admin Long adminId,
        @RequestParam(required = false) TargetType targetType,
        @RequestParam(required = false) ActionType actionType,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        AdminLogResDto response = adminService.getLogs(targetType, actionType, from, to, pageable);
        return ResponseEntity.ok(response);
    }
}
