package com.ssginc.unnie.shop.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ssginc.unnie.common.util.EnumDescription;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ShopCategory implements EnumDescription {
    NAIL_SHOP("네일샵"),
    WAXING_SHOP("왁싱샵"),
    HAIR_SHOP("헤어샵"),
    ESTHETIC("에스테틱"),
    BARBERSHOP("바버샵");

    private final String description;

    ShopCategory(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ShopCategory fromDescription(String description) {
        return Arrays.stream(ShopCategory.values())
                .filter(category -> category.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant for description: " + description));
    }
}