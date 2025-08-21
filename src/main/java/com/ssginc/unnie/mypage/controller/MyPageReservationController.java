package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.reservation.DesignerScheduleDto;
import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;
import com.ssginc.unnie.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageReservationController {

    private final MyPageReservationService myPageReservationService;

    @GetMapping("/reservations")
    public ResponseEntity<ResponseDto<List<ReservationResponse>>> getMyReservations(
            @AuthenticationPrincipal MemberPrincipal principal) {
        List<ReservationResponse> reservations = myPageReservationService.getMyReservations(principal.getMemberId());
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "내 예약 목록 조회 성공", reservations)
        );
    }

    @PatchMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseDto<Map<String, Long>>> updateReservationDateTime(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody ReservationUpdateRequest request) {
        Long memberId = principal.getMemberId();
        Long updatedReservationId = myPageReservationService.updateReservationDateTime(reservationId, memberId, request);
        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "예약이 성공적으로 변경되었습니다.",
                        Map.of("reservationId", updatedReservationId)
                )
        );
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseDto<Object>> cancelReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal MemberPrincipal userDetails) {

        Long memberId = userDetails.getMemberId();
        Long deletedReservationId = myPageReservationService.cancelReservation(reservationId, memberId);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "예약이 성공적으로 변경되었습니다.",
                        Map.of("reservationId", deletedReservationId)
                )
        );
    }

    @GetMapping("/manager/reservations/dashboard")
    public ResponseEntity<ResponseDto<List<DesignerScheduleDto>>> getDailyReservationsForDashboard(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam int shopId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        List<DesignerScheduleDto> dailySchedule = myPageReservationService.getDailySchedule(principal.getMemberId(), shopId, date);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "일일 예약 현황 조회 성공", dailySchedule)
        );
    }


}
