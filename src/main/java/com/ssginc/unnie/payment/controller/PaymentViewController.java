package com.ssginc.unnie.payment.controller;

import com.ssginc.unnie.payment.dto.PaymentSuccess;
import com.ssginc.unnie.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class PaymentViewController {

    private final PaymentService paymentService;

    @GetMapping("/complete")
    public String paymentCompletePage(@RequestParam String orderId, Model model) {
        PaymentSuccess details = paymentService.getPaymentSuccessDetails(orderId);
        model.addAttribute("details", details);
        return "payment/success";
    }

    @GetMapping("/fail")
    public String paymentFailPage(@RequestParam String message, Model model) {
        model.addAttribute("errorMessage", message);
        return "payment/fail";
    }
}