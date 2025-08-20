package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MyPageReservationMapper {

        // [추가] memberId로 예약 목록 조회
        List<ReservationResponse> findReservationsByMemberId(@Param("memberId") Long memberId);
}