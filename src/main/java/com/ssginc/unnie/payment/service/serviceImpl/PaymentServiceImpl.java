package com.ssginc.unnie.payment.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssginc.unnie.common.exception.UnniePaymentException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.payment.dto.*;
import com.ssginc.unnie.payment.mapper.PaymentMapper;
import com.ssginc.unnie.payment.service.PaymentService;
import com.ssginc.unnie.reservation.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentIntentCreateResponse createIntent(PaymentIntentCreateRequest req) {

        // 1. 예약 정보 및 결제 금액 조회
        Integer amount = paymentMapper.selectProcedurePrice(req.getReservationId());
        if (amount == null) {
            throw new UnniePaymentException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        // 2. 결제 금액 유효성 검사
        if (amount <= 0) {
            throw new UnniePaymentException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // 3. 주문명 생성
        String orderName = paymentMapper.selectOrderName(req.getReservationId());
        if (orderName == null || orderName.isBlank()) {
            orderName = "예약-" + req.getReservationId();
        }

        // 4. 결제 제공자(PG사) 설정
        if (req.getProvider() == null || req.getProvider().isBlank()) {
            req.setProvider("TOSS");
        }

        // 5. 결제 의도(Intent) 생성 및 DB 저장
        paymentMapper.insertPaymentIntent(
                req.getReservationId(), req.getMemberId(), req.getShopId(),
                req.getOrderId(), req.getProvider(), amount
        );
        Long intentId = paymentMapper.selectIntentIdByOrderId(req.getOrderId());
        if (intentId == null) {
            throw new UnniePaymentException(ErrorCode.PAYMENT_INTENT_CREATION_FAILED);
        }
        paymentMapper.insertReservationPayment(req.getReservationId(), intentId);

        // 6. 리다이렉트 URL 생성 및 반환
        String redirectUrl = "/pay/" + req.getProvider().toLowerCase() + "?orderId=" + req.getOrderId();
        return new PaymentIntentCreateResponse(intentId, req.getOrderId(), redirectUrl, amount, orderName);
    }


    @Override
    @Transactional
    public void approve(PaymentApproveRequest req) {

        // 1. 결제 의도를 'PAID' 상태로 업데이트
        int upd = paymentMapper.updateIntentAsPaid(req.getOrderId(), req.getProviderTid(), req.getProvider());
        if (upd == 0) {
            throw new UnniePaymentException(ErrorCode.PAYMENT_INTENT_NOT_FOUND);
        }

        Long intentId = paymentMapper.selectIntentIdByOrderId(req.getOrderId());
        if (intentId == null) {
            throw new UnniePaymentException(ErrorCode.PAYMENT_INTENT_NOT_FOUND);
        }

        // 2. 결제 이벤트 로그 저장
        try {
            String payload = objectMapper.writeValueAsString(req);
            paymentMapper.insertPaymentEvent(intentId, "APPROVE", payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for orderId: {}", req.getOrderId(), e);
            // Checked Exception을 우리가 만든 Unchecked Exception으로 변환하여 던짐
            throw new UnniePaymentException(ErrorCode.PAYLOAD_SERIALIZE_FAILED, e);
        }

        // 3. 예약 상태를 'CONFIRMED'로 최종 확정
        Long reservationId = paymentMapper.selectReservationIdByOrderId(req.getOrderId());
        int confirmResult = reservationMapper.confirmReservationPaid(reservationId, intentId);

        if (confirmResult == 0) {
            throw new UnniePaymentException(ErrorCode.PAYMENT_CONFIRMATION_FAILED);
        }
    }

    @Override
    public PaymentSuccess getPaymentSuccessDetails(String orderId) {
        PaymentSuccess details = paymentMapper.selectSuccessInfoByOrderId(orderId);
        if (details == null) {
            throw new UnniePaymentException(ErrorCode.PAYMENT_DETAILS_NOT_FOUND);
        }
        return details;
    }
}