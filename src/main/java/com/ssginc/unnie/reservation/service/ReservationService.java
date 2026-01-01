package com.ssginc.unnie.reservation.service;

import com.ssginc.unnie.reservation.dto.ReservationHoldRequest;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReservationService {
    Long createHold(ReservationHoldRequest req);

    List<String> getBookedTimes(int designerId, String date);

    String getReserverNameByMemberId(Long memberId);


    @Transactional(readOnly = true)
    com.ssginc.unnie.reservation.dto.ReservationResponse getReservationById(Long reservationId);
}
