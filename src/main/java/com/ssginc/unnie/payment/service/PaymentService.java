package com.ssginc.unnie.payment.service;

import com.ssginc.unnie.payment.dto.*;

public interface PaymentService {
    PaymentIntentCreateResponse createIntent(PaymentIntentCreateRequest req);

    void approve(PaymentApproveRequest req);

}