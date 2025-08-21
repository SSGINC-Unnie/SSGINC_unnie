package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;

import java.util.List;

public interface MyPageReservationService {
    List<ReservationResponse> getMyReservations(Long memberId);

    Long cancelReservation(Long reservationId, Long memberId);

    Long updateReservationDateTime(Long reservationId, Long memberId, ReservationUpdateRequest request);

}