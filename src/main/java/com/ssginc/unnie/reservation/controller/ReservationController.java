package com.ssginc.unnie.reservation.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.reservation.dto.ReservationHoldRequest;
import com.ssginc.unnie.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 홀드(결제 대기) 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<Map<String, Object>>> createReservationHold(
            @RequestBody ReservationHoldRequest request) {

        Long reservationId = reservationService.createHold(request);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "예약 홀드 생성에 성공했습니다.",
                        Map.of("reservationId", reservationId, "status", "HOLD")
                )
        );
    }
}
