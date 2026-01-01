package com.ssginc.unnie.reservation.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.exception.UnnieReservationException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.reservation.dto.ReservationHoldRequest;
import com.ssginc.unnie.reservation.dto.ReservationIdRow;
import com.ssginc.unnie.reservation.mapper.ReservationMapper;
import com.ssginc.unnie.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public Long createHold(ReservationHoldRequest req) {
        try {
            ReservationIdRow row = reservationMapper.createReservationHold(
                    req.getMemberId(), req.getShopId(), req.getDesignerId(),
                    req.getProcedureId(), req.getStartTime(), req.getHoldMinutes()
            );

            if (row == null || row.getReservationId() == null) {
                throw new UnnieReservationException(ErrorCode.RESERVATION_SLOT_UNAVAILABLE);
            }
            return row.getReservationId();

        } catch (DataAccessException e) {
            String msg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();

            if (msg != null && msg.contains("Duplicate entry")) {
                log.warn("예약 시간 중복 발생: {}", msg);
                throw new UnnieReservationException(ErrorCode.RESERVATION_SLOT_UNAVAILABLE);
            }

            log.error("예약 홀드 생성 중 데이터베이스 오류 발생: {}", msg, e);
            throw new UnnieReservationException(ErrorCode.RESERVATION_HOLD_FAILED);
        }
    }

    @Override
    public List<String> getBookedTimes(int designerId, String date) {
        try {
            List<LocalDateTime> bookedDateTimes = reservationMapper.findBookedTimesByDesignerAndDate(designerId, date);

            return bookedDateTimes.stream()
                    .map(ldt -> ldt.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .collect(Collectors.toList());

        } catch (DataAccessException e) {
            log.error("예약된 시간 조회 중 데이터베이스 오류 발생. designerId={}, date={}", designerId, date, e);
            throw new UnnieReservationException(ErrorCode.BOOKED_TIMES_FETCH_FAILED, e);
        }
    }

    @Override
    public String getReserverNameByMemberId(Long memberId) {
        String memberName = memberMapper.selectMemberNameByMemberId(memberId);
        if (memberName == null) {
            throw new UnnieMemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return memberName;
    }

    @Transactional(readOnly = true)
    @Override
    public com.ssginc.unnie.reservation.dto.ReservationResponse getReservationById(Long reservationId) {
        return reservationMapper.findReservationById(reservationId);
    }

}