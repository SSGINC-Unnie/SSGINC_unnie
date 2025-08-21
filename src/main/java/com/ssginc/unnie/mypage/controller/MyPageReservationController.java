package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;
import com.ssginc.unnie.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage/reservations")
@RequiredArgsConstructor
public class MyPageReservationController {

    private final MyPageReservationService myPageReservationService;
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<ReservationResponse>>> getMyReservations(
            @AuthenticationPrincipal MemberPrincipal principal) {
        List<ReservationResponse> reservations = myPageReservationService.getMyReservations(principal.getMemberId());
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "내 예약 목록 조회 성공", reservations)
        );
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ResponseDto<Map<String, Long>>> updateReservationDateTime(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody ReservationUpdateRequest request) {
        Long memberId = principal.getMemberId();
        Long updatedReservationId = reservationService.updateReservationDateTime(reservationId, memberId, request);
        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "예약이 성공적으로 변경되었습니다.",
                        Map.of("reservationId", updatedReservationId)
                )
        );
    }
}
