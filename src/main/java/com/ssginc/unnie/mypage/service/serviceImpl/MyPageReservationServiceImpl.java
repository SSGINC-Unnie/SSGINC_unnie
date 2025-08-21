package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.exception.UnnieReservationException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.mypage.mapper.MyPageReservationMapper;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageReservationServiceImpl implements MyPageReservationService {

    private final MyPageReservationMapper myPageReservationMapper;

    @Override
    public List<ReservationResponse> getMyReservations(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new UnnieMemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        try {
            List<ReservationResponse> reservations = myPageReservationMapper.findReservationsByMemberId(memberId);
            if (reservations == null) {
                return Collections.emptyList();
            }
            return reservations;
        } catch (DataAccessException e) {
            log.error("내 예약 목록 조회 중 데이터베이스 오류 발생. memberId={}", memberId, e);
            throw new UnnieReservationException(ErrorCode.MY_RESERVATIONS_FETCH_FAILED, e);
        }
    }
}