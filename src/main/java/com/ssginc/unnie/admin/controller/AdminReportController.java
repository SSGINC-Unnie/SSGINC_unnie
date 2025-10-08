package com.ssginc.unnie.admin.controller;

import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.service.AdminReportService;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // [추가] 예외 임포트
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

    /**
     * 신고 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer targetType,
            @RequestParam(required = false) String status,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        String role = getAdminRole(memberPrincipal);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "신고 목록 조회 성공", Map.of("reports", adminReportService.getAllReports(targetType, status, startDate, endDate, page, role)))
        );
    }

    /**
     * 신고 상세 조회
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReportById(
            @PathVariable("reportId") long reportId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        String role = getAdminRole(memberPrincipal);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "신고 상세 조회 성공", Map.of("report", adminReportService.getReportById(reportId, role)))
        );
    }

    /**
     * 신고 무시
     */
    @PatchMapping("/{reportId}")
    public ResponseEntity<SimpleResponseDto> ignoreReport(
            @PathVariable("reportId") long reportId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        String role = getAdminRole(memberPrincipal);

        adminReportService.ignoreReport(reportId, role);

        return ResponseEntity.ok(
                new SimpleResponseDto(HttpStatus.OK.value(), "신고 무시 성공")
        );
    }

    /**
     * 신고된 컨텐츠 삭제(soft delete)
     */
    @DeleteMapping()
    public ResponseEntity<SimpleResponseDto> softDeleteReportById(
            @RequestBody AdminReportDeleteRequest report,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        String role = getAdminRole(memberPrincipal);

        adminReportService.softDeleteReportById(report, role);

        return ResponseEntity.ok(
                new SimpleResponseDto(HttpStatus.OK.value(), "신고 컨텐츠 삭제 성공")
        );
    }

    /**
     * 관리자 알림 목록 조회
     */
    @GetMapping("/notifications")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAdminNotifications(
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        getAdminRole(memberPrincipal);
        long adminId = memberPrincipal.getMemberId();

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "관리자 알림 목록 조회 성공", Map.of("notifications", adminReportService.getAdminNotifications(adminId, page)))
        );
    }

    /**
     * [신규] 관리자 권한을 확인하고 반환하는 private 헬퍼 메서드
     */
    private String getAdminRole(MemberPrincipal memberPrincipal) {
        if (memberPrincipal == null) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }
        String role = memberPrincipal.getAuthorities().iterator().next().getAuthority();
        if (!"ROLE_ADMIN".equals(role)) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }
        return "ADMIN";
    }
}