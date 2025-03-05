package com.ssginc.unnie.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopViewController {

    @GetMapping("/shop-map")
    public String getShopMapPage(Model model) {
        // HTML에 전달할 추가 데이터가 필요하면 여기에 추가할 수 있습니다.
        return "map/map";
    }
}
