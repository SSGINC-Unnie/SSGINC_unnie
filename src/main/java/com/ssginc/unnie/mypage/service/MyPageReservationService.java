package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import java.util.List;

public interface MyPageReservationService {
    List<ReservationResponse> getMyReservations(Long memberId);
}