package com.ssginc.unnie.reservation.mapper;

import com.ssginc.unnie.reservation.dto.ReservationIdRow;
import com.ssginc.unnie.reservation.dto.ReservationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

    ReservationIdRow createReservationHold(
            @Param("memberId") Long memberId,
            @Param("shopId") Integer shopId,
            @Param("designerId") Integer designerId,
            @Param("procedureId") Integer procedureId,
            @Param("startTime") LocalDateTime startTime,
            @Param("holdMinutes") Integer holdMinutes
    );

    int confirmReservationPaid(@Param("reservationId") Long reservationId,
                               @Param("intentId") Long intentId);

    void cancelReservationByUser(@Param("reservationId") Long reservationId,
                                 @Param("reason") String reason);

    List<LocalDateTime> findBookedTimesByDesignerAndDate(
            @Param("designerId") int designerId,
            @Param("date") String date
    );

    ReservationResponse findReservationById(Long reservationId);

    int updateExpiredReservationsToCompleted();

}
