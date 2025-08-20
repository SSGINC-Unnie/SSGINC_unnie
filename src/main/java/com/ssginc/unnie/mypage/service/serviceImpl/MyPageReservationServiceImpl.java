package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.mypage.mapper.MyPageReservationMapper;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageReservationServiceImpl implements MyPageReservationService {

    private final MyPageReservationMapper myPageReservationMapper;

    @Override
    public List<ReservationResponse> getMyReservations(Long memberId) {
        return myPageReservationMapper.findReservationsByMemberId(memberId);
    }
}