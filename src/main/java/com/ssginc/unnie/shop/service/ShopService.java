package com.ssginc.unnie.shop.service;

import com.ssginc.unnie.shop.dto.ShopDetailsRequest;
import com.ssginc.unnie.shop.dto.ShopDetailsResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;

import java.util.List;

public interface ShopService {
    List<ShopResponse> selectShopByCategory(String category);

}
