package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MyPageReservationMapper {

        List<ReservationResponse> findReservationsByMemberId(@Param("memberId") Long memberId);

        void cancelReservationByUser(
                @Param("reservationId") Long reservationId,
                @Param("memberId") Long memberId
        );

        void updateReservationDateTime(
                @Param("reservationId") Long reservationId,
                @Param("memberId") Long memberId,
                @Param("newStartTime") LocalDateTime newStartTime
        );
}