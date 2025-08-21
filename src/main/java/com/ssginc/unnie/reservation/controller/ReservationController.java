package com.ssginc.unnie.reservation.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import com.ssginc.unnie.reservation.dto.ReservationHoldRequest;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;
import com.ssginc.unnie.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final MyPageReservationService myPageReservationService;

    /**
     * 예약 홀드(결제 대기) 생성
     */
    @PostMapping("/hold")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createReservationHold(
            @RequestBody ReservationHoldRequest request,
            @AuthenticationPrincipal MemberPrincipal principal) {

        long memberId = principal.getMemberId();
        request.setMemberId(memberId);
        Long reservationId = reservationService.createHold(request);

        Map<String, Object> data = new HashMap<>();
        data.put("reservationId", reservationId);
        data.put("status", "HOLD");

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "예약 홀드 생성에 성공했습니다.", data)
        );
    }


    @GetMapping("/member-info")
    public ResponseEntity<ResponseDto<Map<String, String>>> getMemberInfo(
            @AuthenticationPrincipal MemberPrincipal principal) {

        String memberName = reservationService.getReserverNameByMemberId(principal.getMemberId());

        log.info(memberName);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "Success", Map.of("memberName", memberName))
        );
    }

    @GetMapping("/booked-times")
    public ResponseEntity<ResponseDto<List<String>>> getBookedTimes(
            @RequestParam("designerId") int designerId,
            @RequestParam("date") String date
    ) {
        List<String> bookedTimes = reservationService.getBookedTimes(designerId, date);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "예약된 시간 조회 성공", bookedTimes)
        );
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservationDateTime(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal MemberPrincipal p,
            @Valid @RequestBody ReservationUpdateRequest request) {

        Long memberId = p.getMember().getMemberId();

        myPageReservationService.updateReservationDateTime(reservationId, memberId, request);

        return ResponseEntity.ok().build();
    }
}