package com.ssginc.unnie.reservation.service.serviceImpl;

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
                    req.getMemberId(),
                    req.getShopId(),
                    req.getDesignerId(),
                    req.getProcedureId(),
                    req.getStartTime(),
                    req.getHoldMinutes()
            );
            if (row == null || row.getReservationId() == null) {
                throw new IllegalStateException("예약 ID 반환이 없습니다.");
            }
            return row.getReservationId();
        } catch (DataAccessException e) {
            String msg = e.getMostSpecificCause() != null
                    ? e.getMostSpecificCause().getMessage()
                    : e.getMessage();
            log.error("예약 홀드 생성 실패: {}", msg, e);
            throw new IllegalStateException("예약 생성 실패: " + msg);
        }
    }

    @Override
    public List<String> getBookedTimes(int designerId, String date) {
        List<LocalDateTime> bookedDateTimes = reservationMapper.findBookedTimesByDesignerAndDate(designerId, date);

        return bookedDateTimes.stream()
                .map(ldt -> ldt.format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());
    }

    @Override
    public String getReserverNameByMemberId(Long memberId) {
        if (memberId == null) {
            return null;
        }
        // MemberMapper를 통해 이름을 조회하여 반환
        return memberMapper.selectMemberNameByMemberId(memberId);
    }
}
