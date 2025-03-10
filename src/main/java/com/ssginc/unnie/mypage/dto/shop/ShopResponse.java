package com.ssginc.unnie.mypage.dto.shop;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

@Data
public class ShopResponse {
    private int shopId;
    private String shopName;
    private String shopRepresentationName;
    private String shopLocation;
    private ShopCategory shopCategory;
}
