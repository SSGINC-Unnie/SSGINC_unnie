package com.ssginc.unnie.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/map")
public class ShopViewController {

    @GetMapping("/map")
    public String getShopMapPage(Model model) {
        model.addAttribute("activePage", "map");
        return "shop/map";
    }

    @GetMapping("/shopdetail")
    public String getShopdetailPage(Model model) {
        model.addAttribute("activePage", "map");
        return "shop/shopdetail";}

    @GetMapping("/mypage")
    public String mypage(Model model) {
        model.addAttribute("activePage", "mypage");
        return "mypage/mypage";
    }

}
