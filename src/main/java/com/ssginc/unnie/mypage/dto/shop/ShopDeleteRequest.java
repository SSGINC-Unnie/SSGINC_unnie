package com.ssginc.unnie.mypage.dto.shop;

import lombok.Data;

@Data
public class ShopDeleteRequest {
    private int shopId;
    private int shopMemberId;
}