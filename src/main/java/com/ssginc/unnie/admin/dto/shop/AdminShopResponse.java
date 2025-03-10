package com.ssginc.unnie.admin.dto.shop;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

@Data
public class AdminShopResponse {
    private int shopId;
    private String shopName;
    private String shopRepresentationName;
    private String shopLocation;
    private ShopCategory shopCategory;

}
