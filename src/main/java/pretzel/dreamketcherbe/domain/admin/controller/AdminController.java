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
