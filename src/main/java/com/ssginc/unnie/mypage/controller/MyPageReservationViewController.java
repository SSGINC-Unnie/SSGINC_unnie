package com.ssginc.unnie.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MyPageReservationViewController {

    @GetMapping("/reservations")
    public String myReservationsPage() {
        return "mypage/reservation/getMyReservation";
    }
}