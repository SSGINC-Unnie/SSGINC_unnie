package com.ssginc.unnie.mypage.dto.shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShopRegistrationPendingEvent {
    private final int shopId;
    private final String shopName;
}