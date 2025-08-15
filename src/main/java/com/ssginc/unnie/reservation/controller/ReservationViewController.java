package com.ssginc.unnie.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationViewController {

    @GetMapping
    public String reservationPage(@RequestParam("shopId") int shopId, Model model) {
        log.info("예약 페이지 요청 수신. shopId: {}", shopId);

        model.addAttribute("shopId", shopId);

        return "reservation/reservation";
    }
}