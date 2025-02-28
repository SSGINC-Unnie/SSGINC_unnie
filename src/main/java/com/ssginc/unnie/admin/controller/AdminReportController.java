package com.ssginc.unnie.admin.controller;

import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.service.AdminReportService;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.SimpleResponseDto;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자 신고 관리 컨트롤러 클래스
 */

@Slf4j
@RestController
@RequestMapping("/api/admin/report")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;
    private final ProducerService producerService;

    /**
     * 신고 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int targetType, // 1 : 게시글 / 2: 댓글 / 3: 리뷰
            @RequestParam(required = false) String status, // 0 : 미처리 / 1 : 처리 / 2: 무시
            @RequestParam String startDate, // yyyy-MM-dd
            @RequestParam String endDate,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        String role = "ADMIN";

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "신고 목록 조회 성공", Map.of("reports", adminReportService.getAllReports(targetType, status, startDate, endDate, page, role)))
        );
    }

    /**
     * 신고 상세 조회
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReportById(@PathVariable("reportId") long reportId) {

        String role = "ADMIN";

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "신고 상세 조회 성공", Map.of("report", adminReportService.getReportById(reportId, role)))
        );
    }

    /**
     * 신고 무시
     */
    @PatchMapping("/{reportId}")
    public ResponseEntity<SimpleResponseDto> ignoreReport(@PathVariable("reportId") long reportId) {

        String role = "ADMIN";

        adminReportService.ignoreReport(reportId, role);

        return ResponseEntity.ok(
                new SimpleResponseDto(HttpStatus.OK.value(), "신고 무시 성공")
        );
    }

    /**
     * 신고된 컨텐츠 삭제(soft delete)
     */
    @DeleteMapping()
    public ResponseEntity<SimpleResponseDto> softDeleteReportById(AdminReportDeleteRequest report) {

        String role = "ADMIN";

        adminReportService.softDeleteReportById(report, role);

        NotificationResponse response = adminReportService.getReportTargetMemberInfoByTargetInfo(report);

        NotificationMessage msg = adminReportService.createNotificationMsg(report, response);

        producerService.createNotification(msg);

        return ResponseEntity.ok(
                new SimpleResponseDto(HttpStatus.OK.value(), "신고 컨텐츠 삭제 성공")
        );
    }
}
