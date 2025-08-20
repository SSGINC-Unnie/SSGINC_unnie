package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/mypage/reservations")
@RequiredArgsConstructor
public class MyPageReservationController {

    private final MyPageReservationService myPageReservationService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<ReservationResponse>>> getMyReservations(
            @AuthenticationPrincipal MemberPrincipal principal) {

        List<ReservationResponse> reservations = myPageReservationService.getMyReservations(principal.getMemberId());

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "내 예약 목록 조회 성공", reservations)
        );
    }
}