package com.ssginc.unnie.shop.vo;

import lombok.Getter;

@Getter
public enum ShopCategory {
    NAIL_SHOP("네일숍"),
    WAXING_SHOP("왁싱샵"),
    MASSAGE_SHOP("마사지샵"),
    ESTHETIC("에스테틱"),
    BARBERSHOP("바버샵");

    private final String description;

    ShopCategory(String description) {
        this.description = description;
    }
}