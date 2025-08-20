package com.ssginc.unnie.payment.mapper;

import com.ssginc.unnie.payment.dto.PaymentSuccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    Integer selectProcedurePrice(@Param("reservationId") Long reservationId);

    int insertPaymentIntent(@Param("reservationId") Long reservationId,
                            @Param("memberId") Long memberId,
                            @Param("shopId") Integer shopId,
                            @Param("orderId") String orderId,
                            @Param("provider") String provider,
                            @Param("amount") Integer amount);

    Long selectIntentIdByOrderId(@Param("orderId") String orderId);

    int insertReservationPayment(@Param("reservationId") Long reservationId,
                                 @Param("intentId") Long intentId);

    // --- 승인용 ---
    Long selectReservationIdByOrderId(@Param("orderId") String orderId);
    int updateIntentAsPaid(@Param("orderId") String orderId,
                           @Param("providerTid") String providerTid,
                           @Param("provider") String provider);
    int insertPaymentEvent(@Param("intentId") Long intentId,
                           @Param("eventType") String eventType,
                           @Param("payload") String payloadJson);

    String selectOrderName(@Param("reservationId") Long reservationId);

    PaymentSuccess selectSuccessInfoByOrderId(@Param("orderId") String orderId);


}
