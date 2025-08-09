package com.ssginc.unnie.payment.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper; // 스프링이 주입해줌

    @Override
    @Transactional
    public PaymentIntentCreateResponse createIntent(PaymentIntentCreateRequest req) {
        Integer amount = paymentMapper.selectProcedurePrice(req.getReservationId());
        if (amount == null) throw new IllegalStateException("예약을 찾을 수 없습니다.");

        String orderName = null;
        try { orderName = paymentMapper.selectOrderName(req.getReservationId()); } catch (Exception ignore) {}
        if (orderName == null || orderName.isBlank()) orderName = "예약-" + req.getReservationId();

        if (req.getProvider() == null || req.getProvider().isBlank()) {
            req.setProvider("TOSS");
        }

        paymentMapper.insertPaymentIntent(
                req.getReservationId(), req.getMemberId(), req.getShopId(),
                req.getOrderId(), req.getProvider(), amount
        );
        Long intentId = paymentMapper.selectIntentIdByOrderId(req.getOrderId());
        paymentMapper.insertReservationPayment(req.getReservationId(), intentId);

        String redirectUrl = "/pay/" + req.getProvider().toLowerCase() + "?orderId=" + req.getOrderId();
        // ✅ orderId 넣어서 반환
        return new PaymentIntentCreateResponse(intentId, req.getOrderId(), redirectUrl, amount, orderName);
    }

    @Override
    @Transactional
    public void approve(PaymentApproveRequest req) {
        int upd = paymentMapper.updateIntentAsPaid(req.getOrderId(), req.getProviderTid(), req.getProvider());
        if (upd == 0) throw new IllegalStateException("orderId에 해당하는 결제의도가 없습니다.");

        try {
            String payload = objectMapper.writeValueAsString(req);
            Long intentId = paymentMapper.selectIntentIdByOrderId(req.getOrderId());
            paymentMapper.insertPaymentEvent(intentId, "APPROVE", payload);

            Long reservationId = paymentMapper.selectReservationIdByOrderId(req.getOrderId());
            reservationMapper.confirmReservationPaid(reservationId, intentId);
        } catch (Exception e) {
            log.error("승인 처리 중 오류", e);
            throw new IllegalStateException("승인 처리 실패: " + e.getMessage());
        }
    }
}
