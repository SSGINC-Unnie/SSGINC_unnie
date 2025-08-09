package com.ssginc.unnie.reservation.service;

import com.ssginc.unnie.reservation.dto.ReservationHoldRequest;

public interface ReservationService {
    Long createHold(ReservationHoldRequest req);
}
