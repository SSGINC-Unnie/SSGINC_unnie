package com.ssginc.unnie.mypage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class myPageShopViewController {

    @GetMapping("/shop")
    public String CreateShop() {
        return "mypage/shop/shopInsert";
    }

    @GetMapping("/designer/{shopId}")
    public String CreateDesigner(@PathVariable("shopId") int shopId, Model model) {
        model.addAttribute("shopId", shopId);
        return "mypage/shop/designerInsert";
    }

    @GetMapping("/procedure/{shopId}")
    public String CreateProcedure(@PathVariable("shopId") int shopId, Model model) {
        model.addAttribute("shopId", shopId);
        return "mypage/shop/procedureInsert";
    }

    @GetMapping("/myshop")
    public String GetSHops() {
        return "mypage/shop/getshop";
    }

}
