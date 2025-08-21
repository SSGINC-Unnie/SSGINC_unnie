package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.reservation.DesignerScheduleDto;
import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;

import java.time.LocalDate;
import java.util.List;

public interface MyPageReservationService {
    List<ReservationResponse> getMyReservations(Long memberId);

    Long cancelReservation(Long reservationId, Long memberId);

    Long updateReservationDateTime(Long reservationId, Long memberId, ReservationUpdateRequest request);

    List<DesignerScheduleDto> getDailySchedule(Long memberId, int shopId, LocalDate date);

}