package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieException;
import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.exception.UnnieReservationException;
import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.reservation.DesignerScheduleDto;
import com.ssginc.unnie.mypage.dto.reservation.MyPageReservationManagedto;
import com.ssginc.unnie.mypage.dto.reservation.ReservationResponse;
import com.ssginc.unnie.mypage.dto.shop.MyDesignerDetailResponse;
import com.ssginc.unnie.mypage.mapper.MyPageReservationMapper;
import com.ssginc.unnie.mypage.mapper.MyPageShopMapper;
import com.ssginc.unnie.mypage.service.MyPageReservationService;
import com.ssginc.unnie.reservation.dto.ReservationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageReservationServiceImpl implements MyPageReservationService {

    private final MyPageReservationMapper myPageReservationMapper;
    private final MyPageShopMapper myPageShopMapper;


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

    @Override
    @Transactional
    public Long updateReservationDateTime(Long reservationId, Long memberId, ReservationUpdateRequest request) {
        try {
            myPageReservationMapper.updateReservationDateTime(
                    reservationId,
                    memberId,
                    request.getNewStartTime()
            );
        } catch (DataAccessException e) {
            String msg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
            log.error("예약 변경 실패. reservationId={}, memberId={}, msg={}", reservationId, memberId, msg, e);

            if (msg != null) {
                if (msg.contains("Slot not available")) {
                    throw new UnnieReservationException(ErrorCode.RESERVATION_SLOT_UNAVAILABLE);
                }
                if (msg.contains("Reservation not modifiable")) {
                    throw new UnnieReservationException(ErrorCode.RESERVATION_NOT_MODIFIABLE);
                }
            }
        }
        return reservationId;
    }

    @Override
    @Transactional
    public Long cancelReservation(Long reservationId, Long memberId) {
        try {
            myPageReservationMapper.cancelReservationByUser(reservationId, memberId);
        } catch (DataAccessException e) {
            log.error("예약 취소 실패. reservationId={}, memberId={}", reservationId, memberId, e);
            throw new UnnieReservationException(ErrorCode.RESERVATION_CANCEL_FAILED);
        }
        return reservationId;
    }

    @Override
    @Transactional

    public List<DesignerScheduleDto> getDailySchedule(Long memberId, int shopId, LocalDate date) {
        int ownerCount = myPageShopMapper.checkShopOwnership(shopId, memberId);
        if (ownerCount == 0) {
            // 본인 소유의 매장이 아니면 권한 없음 에러 발생
            throw new UnnieShopException(ErrorCode.SHOP_OWNERSHIP_REQUIRED);
        }

        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<MyPageReservationManagedto> allReservations = myPageReservationMapper.findReservationsForDashboard(shopId, dateString);
        List<MyDesignerDetailResponse> allDesigners = myPageShopMapper.findDesignersByShopId(shopId);

        return allDesigners.stream().map(designer -> {
            DesignerScheduleDto schedule = new DesignerScheduleDto();
            schedule.setDesignerId(designer.getDesignerId());
            schedule.setDesignerName(designer.getDesignerName());

            List<MyPageReservationManagedto> designerReservations = allReservations.stream()
                    .filter(res -> designer.getDesignerId().equals(res.getDesignerId()))
                    .peek(res -> {
                        if (res.getStartTime() != null) {
                            res.setEndTime(res.getStartTime().plusMinutes(60));
                        }
                    })
                    .collect(Collectors.toList());
            schedule.setReservations(designerReservations);
            return schedule;
        }).collect(Collectors.toList());
    }


}