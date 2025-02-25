package com.ssginc.unnie.report.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.report.dto.ReportRequest;
import com.ssginc.unnie.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 신고 기능 관련 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createReport(ReportRequest report) {

        long memberId = 1;

        report.setReportMemberId(memberId);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "신고가 접수되었습니다.", Map.of("reportId", reportService.createReport(report)))
        );
    }

}
