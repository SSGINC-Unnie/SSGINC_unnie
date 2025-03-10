package com.ssginc.unnie.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminShopViewController {


    @GetMapping("/Allshop")
    public String GetShops() {
        return "admin/shop/getAllShop";
    }

    @GetMapping("/approve")
    public String getApproveShops() {
        return "admin/shop/getApproveShop";
    }
}
